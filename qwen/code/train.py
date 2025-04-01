import os
import math
import pickle
from pathlib import Path
from transformers import AutoTokenizer, AutoModelForCausalLM, TrainingArguments, Trainer
from peft import LoraConfig, get_peft_model, prepare_model_for_kbit_training
import torch
from datasets import load_dataset

# 设置环境变量，避免tokenizer并行警告
os.environ["TOKENIZERS_PARALLELISM"] = "false"

# 模型和数据路径
model_path = "../premodels"
data_path = "../datasets/92502saf/alpaca_format.json"
output_dir = "./output"

def preprocess_function(examples):
    prompts = []
    for i in range(len(examples['instruction'])):
        prompt = f"System: {examples['system'][i]}\n"
        prompt += f"Instruction: {examples['instruction'][i]}\n"
        prompt += f"Input: {examples['input'][i]}\n"
        prompt += f"Output: {examples['output'][i]}"
        prompts.append(prompt)
    
    tokenized = tokenizer(
        prompts,
        truncation=True,
        max_length=2048,
        padding="max_length",
        return_tensors="pt"
    )
    tokenized["labels"] = tokenized["input_ids"].clone()
    return tokenized

def get_last_checkpoint():
    """获取最新的检查点"""
    if not os.path.exists(output_dir):
        return None
    checkpoints = [d for d in os.listdir(output_dir) if d.startswith("checkpoint")]
    if not checkpoints:
        return None
    return os.path.join(output_dir, sorted(checkpoints)[-1])

def get_current_shard():
    """获取当前应该训练的分片编号"""
    if not os.path.exists(output_dir):
        return 0
    shard_files = [f for f in os.listdir(output_dir) if f.startswith("shard_") and f.endswith("_complete.txt")]
    if not shard_files:
        return 0
    completed_shards = [int(f.split("_")[1]) for f in shard_files]
    return max(completed_shards) + 1

def train_with_shards(dataset, shard_size=100):  # 增加分片大小到100
    total_samples = len(dataset)
    num_shards = math.ceil(total_samples / shard_size)
    
    current_shard = get_current_shard()
    print(f"从分片 {current_shard}/{num_shards} 开始训练")
    
    # 优化训练参数
    training_args = TrainingArguments(
        output_dir=output_dir,
        num_train_epochs=1,
        per_device_train_batch_size=2,  # 增加批次大小
        gradient_accumulation_steps=4,   # 减少梯度累积步数
        learning_rate=5e-5,             # 增加学习率
        logging_steps=2,
        save_strategy="steps",
        save_steps=10,
        eval_strategy="no",
        remove_unused_columns=False,
        max_steps=10,                    # 减少每个分片的训练步数
        warmup_steps=2,
        weight_decay=0.01,
        fp16=False,
        dataloader_num_workers=0,
        gradient_checkpointing=True      # 启用梯度检查点以节省内存
    )
    
    # 创建训练器
    trainer = Trainer(
        model=model,
        args=training_args,
        tokenizer=tokenizer
    )
    
    # 分片训练
    for i in range(current_shard, num_shards):
        start_idx = i * shard_size
        end_idx = min((i + 1) * shard_size, total_samples)
        print(f"\n训练分片 {i+1}/{num_shards} (样本 {start_idx} 到 {end_idx})")
        
        # 获取当前分片的数据
        current_shard_data = dataset.select(range(start_idx, end_idx))
        trainer.train_dataset = current_shard_data
        
        # 训练当前分片
        trainer.train(resume_from_checkpoint=get_last_checkpoint() if i == current_shard else None)
        
        # 保存分片完成标记
        with open(os.path.join(output_dir, f"shard_{i}_complete.txt"), "w") as f:
            f.write(f"Completed shard {i+1}/{num_shards}")
        
        print(f"分片 {i+1} 训练完成")

# 添加缓存路径
cache_dir = "./cache"
processed_data_cache = os.path.join(cache_dir, "processed_dataset.pkl")

def load_or_process_dataset(data_path):
    """加载或处理数据集，使用缓存加速"""
    # 确保缓存目录存在
    os.makedirs(cache_dir, exist_ok=True)
    
    # 尝试加载缓存
    if os.path.exists(processed_data_cache):
        print("发现预处理数据缓存，尝试加载...")
        try:
            with open(processed_data_cache, 'rb') as f:
                dataset = pickle.load(f)
            print("缓存加载成功！")
            return dataset
        except Exception as e:
            print(f"缓存加载失败: {e}")
            print("删除损坏的缓存文件...")
            os.remove(processed_data_cache)
    
    print("开始处理数据集...")
    # 加载并处理数据集
    dataset = load_dataset('json', data_files=data_path)
    tokenized_dataset = dataset["train"].map(
        preprocess_function,
        batched=True,
        remove_columns=dataset["train"].column_names,
        num_proc=1
    )
    
    # 保存处理后的数据集
    print("保存处理后的数据集到缓存...")
    try:
        with open(processed_data_cache, 'wb') as f:
            pickle.dump(tokenized_dataset, f)
        print("缓存保存成功！")
    except Exception as e:
        print(f"缓存保存失败: {e}")
        if os.path.exists(processed_data_cache):
            os.remove(processed_data_cache)
    
    return tokenized_dataset


if __name__ == "__main__":
    # 1. 加载tokenizer
    tokenizer = AutoTokenizer.from_pretrained(
        model_path, 
        trust_remote_code=True,
        local_files_only=True,
        use_fast=False
    )
    
    # 2. 使用缓存机制加载数据集
    tokenized_dataset = load_or_process_dataset(data_path)
    
    # 3. 加载模型
    model = AutoModelForCausalLM.from_pretrained(
        model_path,
        trust_remote_code=True,
        device_map="auto",
        local_files_only=True,
        use_cache=False,
        torch_dtype=torch.float32  # 确保使用 float32
    )
    
    # 4. 配置LoRA
    lora_config = LoraConfig(
        r=8,
        lora_alpha=32,
        target_modules=["q_proj", "k_proj", "v_proj", "o_proj"],
        lora_dropout=0.1,
        bias="none",
        task_type="CAUSAL_LM"
    )
    
    # 5. 准备LoRA训练
    model = prepare_model_for_kbit_training(model)
    model = get_peft_model(model, lora_config)
    
    # 确保模型处于训练模式并设置梯度
    model.train()
    for name, param in model.named_parameters():
        if 'lora_' in name:
            param.requires_grad = True
    
    # 6. 开始分片训练
    train_with_shards(tokenized_dataset, shard_size=15)
    # 1. 加载tokenizer
    tokenizer = AutoTokenizer.from_pretrained(
        model_path, 
        trust_remote_code=True,
        local_files_only=True,
        use_fast=False
    )
    
    # 2. 使用缓存机制加载数据集
    tokenized_dataset = load_or_process_dataset(data_path)
    
    # 3. 加载模型
    model = AutoModelForCausalLM.from_pretrained(
        model_path,
        trust_remote_code=True,
        device_map="auto",
        local_files_only=True,
        use_cache=False
    )
    
    # 4. 配置LoRA
    lora_config = LoraConfig(
        r=8,
        lora_alpha=32,
        target_modules=["q_proj", "k_proj", "v_proj", "o_proj"],
        lora_dropout=0.1,
        bias="none",
        task_type="CAUSAL_LM"
    )
    
    # 5. 准备LoRA训练
    model = prepare_model_for_kbit_training(model)
    model = get_peft_model(model, lora_config)
    
    # 6. 开始分片训练
    train_with_shards(tokenized_dataset, shard_size=15)
import os
# 设置环境变量，避免tokenizer并行警告
os.environ["TOKENIZERS_PARALLELISM"] = "false"

from transformers import AutoTokenizer, AutoModelForCausalLM, TrainingArguments, Trainer
from peft import LoraConfig, get_peft_model, prepare_model_for_kbit_training
import torch
from datasets import load_dataset

# 模型路径
model_path = "premodels"

# 1. 加载tokenizer
tokenizer = AutoTokenizer.from_pretrained(
    model_path, 
    trust_remote_code=True,
    local_files_only=True,
    use_fast=False  # 使用慢速tokenizer避免并行问题
)

# 2. 数据处理函数
def preprocess_function(examples):
    # 构建提示模板
    prompts = []
    for i in range(len(examples['instruction'])):
        # 按照Alpaca格式构建训练样本
        prompt = f"System: {examples['system'][i]}\n"
        prompt += f"Instruction: {examples['instruction'][i]}\n"
        prompt += f"Input: {examples['input'][i]}\n"
        prompt += f"Output: {examples['output'][i]}"
        prompts.append(prompt)
    
    # tokenize处理
    tokenized = tokenizer(
        prompts,
        truncation=True,
        max_length=2048,
        padding="max_length",
        return_tensors="pt"
    )
    
    # 设置标签，用于计算损失
    tokenized["labels"] = tokenized["input_ids"].clone()
    return tokenized

# 3. 加载并处理数据集
dataset = load_dataset('json', data_files='datasets/92502saf/alpaca_format.json')
tokenized_dataset = dataset["train"].map(
    preprocess_function,
    batched=True,
    remove_columns=dataset["train"].column_names,
    num_proc=1  # 单进程处理
)

# 4. 加载基础模型
model = AutoModelForCausalLM.from_pretrained(
    model_path,
    trust_remote_code=True,
    device_map="auto",
    local_files_only=True,
    use_cache=False  # 禁用KV缓存
)

# 5. 配置LoRA
lora_config = LoraConfig(
    r=8,  # LoRA秩
    lora_alpha=32,  # LoRA alpha参数
    target_modules=["q_proj", "k_proj", "v_proj", "o_proj"],  # 需要训练的模块
    lora_dropout=0.1,
    bias="none",
    task_type="CAUSAL_LM"
)

# 6. 准备LoRA训练
model = prepare_model_for_kbit_training(model)
model = get_peft_model(model, lora_config)

# 7. 设置训练参数
training_args = TrainingArguments(
    output_dir="./output",  # 输出目录
    num_train_epochs=3,  # 训练轮数
    per_device_train_batch_size=2,  # 批次大小
    gradient_accumulation_steps=4,  # 梯度累积
    learning_rate=2e-5,  # 学习率
    logging_steps=10,  # 日志记录间隔
    save_strategy="epoch",  # 保存策略
    eval_strategy="no",  # 不进行评估
    remove_unused_columns=False
)

# 8. 创建训练器
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_dataset,
    tokenizer=tokenizer
)

# 9. 开始训练
if __name__ == "__main__":
    trainer.train()
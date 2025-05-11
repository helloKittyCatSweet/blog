import os
import json
import random

def convert_json_to_jsonl(json_file, output_file, max_samples=2000):
    if not os.path.exists(json_file):
        print(f"错误：找不到文件 {json_file}")
        return

    try:
        # 确保输出目录存在
        os.makedirs(os.path.dirname(output_file), exist_ok=True)
        
        # 先读取所有数据
        all_data = []
        with open(json_file, 'r', encoding='utf-8') as f:
            for line in f:
                try:
                    data = json.loads(line.strip())
                    all_data.append(data)
                except json.JSONDecodeError as e:
                    print(f"跳过无效的JSON行: {e}")
                    continue
        
        # 随机选取数据
        selected_data = random.sample(all_data, min(max_samples, len(all_data)))
        
        # 写入选中的数据
        with open(output_file, 'w', encoding='utf-8') as out_f:
            for data in selected_data:
                simple_item = {
                    "input": "你是一个专业的文章摘要生成助手，请生成准确、简洁的摘要。请仔细阅读以下文章，"
                             "生成一个准确、完整且简洁的摘要。摘要应当：\n1. 包含文章的主要论点和核心观点\n2. "
                             "保留关键的事实和数据\n3. 使用简洁清晰的语言\n4. 确保摘要的逻辑性和连贯性。" + data['text'],
                    "target": data['summary']
                }
                out_f.write(json.dumps(simple_item, ensure_ascii=False) + '\n')

        print(f"成功将数据转换为JSONL格式并保存到 {output_file}")
        print(f"共处理 {len(selected_data)} 条数据")

    except Exception as e:
        print(f"转换过程中出现错误：{e}")
        return

if __name__ == "__main__":
    convert_json_to_jsonl(
        "originDataset/dataset.json", 
        "../dataset/92505saf/simple_format.jsonl",
        500
    )
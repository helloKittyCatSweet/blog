import os
import json

def convert_json_to_alpaca(json_file, output_file):
    if not os.path.exists(json_file):
        print(f"错误：找不到文件 {json_file}")
        return

    try:
        alpaca_data = []
        
        # 逐行读取JSON
        with open(json_file, 'r', encoding='utf-8') as f:
            for line in f:
                try:
                    data = json.loads(line.strip())
                    
                    # 创建Alpaca格式的数据项
                    alpaca_item = {
                        "instruction": "请仔细阅读以下文章，生成一个准确、完整且简洁的摘要。摘要应当：\n1. 包含文章的主要论点和核心观点\n2. 保留关键的事实和数据\n3. 使用简洁清晰的语言\n4. 确保摘要的逻辑性和连贯性",
                        "input": f"正文：{data['text']}",
                        "output": f"{data['summary']}",
                        "system": "你是一个专业的文章摘要生成助手，请生成准确、简洁的摘要。",
                        "history": []
                    }
                    
                    alpaca_data.append(alpaca_item)
                except json.JSONDecodeError as e:
                    print(f"跳过无效的JSON行: {e}")
                    continue

        # 将数据写入JSON文件
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(alpaca_data, f, ensure_ascii=False, indent=2)

        print(f"成功将数据转换为Alpaca格式并保存到 {output_file}")
        print(f"共处理 {len(alpaca_data)} 条数据")

    except Exception as e:
        print(f"转换过程中出现错误：{e}")
        return

if __name__ == "__main__":
    convert_json_to_alpaca("originDataset/dataset.json", "../dataset/92505saf/alpaca_format.json")
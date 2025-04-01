import os
import json
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

def analyze_dataset(json_file):
    if not os.path.exists(json_file):
        print(f"错误：找不到文件 {json_file}")
        return

    try:
        # 直接读取JSON文件
        with open(json_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # 转换为DataFrame进行分析
        df = pd.DataFrame(data)
        
        # 计算文本长度
        df['input_length'] = df['input'].apply(len)
        df['output_length'] = df['output'].apply(len)
        
        # 基础画图设置
        plt.style.use('default')  # 使用默认样式
        plt.rcParams['font.family'] = 'monospace'
        plt.rcParams['figure.facecolor'] = 'white'
        
        # 创建图形
        plt.figure(figsize=(15, 6))
        
        # 输入长度分布
        plt.subplot(1, 2, 1)
        sns.histplot(data=df['input_length'], bins=50, color='skyblue')
        plt.title('Input(Source) Length Distribution')
        plt.xlabel('Length(characters)')
        plt.ylabel('Count')
        
        # 输出长度分布
        plt.subplot(1, 2, 2)
        sns.histplot(data=df['output_length'], bins=50, color='lightgreen')
        plt.title('Summary Length Distribution')
        plt.xlabel('Length(characters)')
        plt.ylabel('Count')
        
        plt.tight_layout()
        plt.savefig('length_distribution.png', dpi=300, bbox_inches='tight')
        plt.close()
        
        print("\n=== 数据集分析 ===")
        print(f"总样本数：{len(df)}")
        print("\n输入文本长度统计：")
        print(df['input_length'].describe())
        print("\n输出文本长度统计：")
        print(df['output_length'].describe())
        
        # 返回建议的训练参数
        max_input_len = int(df['input_length'].quantile(0.95))
        print("\n=== 训练参数建议 ===")
        print(f"建议 MAX_SOURCE_LENGTH: {min(2048, max_input_len)}")
        print(f"建议 batch_size: {4 if max_input_len < 1024 else 2}")
        print(f"\n分布图已保存为 length_distribution.png")

    except Exception as e:
        print(f"分析数据集时出现错误：{e}")
        return

if __name__ == "__main__":
    analyze_dataset("../datasets/92502saf/alpaca_format.json")
import json
from rouge_chinese import Rouge
import jieba


def compute_rouge(jsonl_file):
    # 读取数据
    references = []
    candidates = []
    skipped = 0

    with open(jsonl_file, 'r', encoding='utf-8') as f:
        for i, line in enumerate(f, 1):
            try:
                data = json.loads(line.strip())
                # 获取目标摘要和生成的摘要
                reference = data['target'].strip()
                candidate = data['input'].split('。摘要应当：')[0].strip()

                # Skip empty texts
                if not reference or not candidate:
                    skipped += 1
                    print(f"Skipping line {i}: Empty text found")
                    continue

                # 使用结巴分词
                reference = ' '.join(jieba.cut(reference))
                candidate = ' '.join(jieba.cut(candidate))

                references.append(reference)
                candidates.append(candidate)

            except Exception as e:
                skipped += 1
                print(f"Error processing line {i}: {str(e)}")
                continue

    if not references or not candidates:
        print("No valid text pairs found for ROUGE calculation!")
        return

    print(f"\nProcessed {len(references)} valid text pairs")
    print(f"Skipped {skipped} invalid entries\n")

    # 初始化 Rouge 计算器
    rouge = Rouge()

    # 计算 ROUGE 分数
    scores = rouge.get_scores(candidates, references, avg=True)

    # 打印结果
    print("ROUGE Scores:")
    print("-" * 40)
    print("ROUGE-1:")
    print(f"F1: {scores['rouge-1']['f'] * 100:.2f}")
    print(f"Precision: {scores['rouge-1']['p'] * 100:.2f}")
    print(f"Recall: {scores['rouge-1']['r'] * 100:.2f}")

    print("\nROUGE-2:")
    print(f"F1: {scores['rouge-2']['f'] * 100:.2f}")
    print(f"Precision: {scores['rouge-2']['p'] * 100:.2f}")
    print(f"Recall: {scores['rouge-2']['r'] * 100:.2f}")

    print("\nROUGE-L:")
    print(f"F1: {scores['rouge-l']['f'] * 100:.2f}")
    print(f"Precision: {scores['rouge-l']['p'] * 100:.2f}")
    print(f"Recall: {scores['rouge-l']['r'] * 100:.2f}")


if __name__ == "__main__":
    print("----------------------------Deepseek------------------------------")
    compute_rouge("../dataset/result/3/deepseek_result.jsonl")
    print("----------------------------Deepseek------------------------------")
    print("----------------------------FineTuning------------------------------")
    compute_rouge("../dataset/result/3/fine_tuning_result.jsonl")
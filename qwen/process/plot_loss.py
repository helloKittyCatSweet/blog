import json
import matplotlib.pyplot as plt

def read_loss_data(file_path):
    steps = []
    losses = []
    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            try:
                data = json.loads(line)
                if 'loss' in data:
                    steps.append(data['current_steps'])
                    losses.append(data['loss'])
            except:
                continue
    return steps, losses

# 设置图表样式
plt.style.use('default')  # Changed from 'seaborn' to 'bmh'
plt.figure(figsize=(12, 6), facecolor='white')

# 读取并绘制三个模型的loss
models = {
    'v1': 'loss/v1.0.jsonl',
    'v2': 'loss/v1.1.jsonl',
    'v3': 'loss/v1.3.jsonl'
}

colors = ['#2ecc71', '#3498db', '#e74c3c']
for (name, path), color in zip(models.items(), colors):
    steps, losses = read_loss_data(path)
    plt.plot(steps, losses, label=name, color=color, alpha=0.8, linewidth=2)

# 设置图表属性
plt.title('Training Loss Comparison', fontsize=14, pad=15)
plt.xlabel('Steps', fontsize=12)
plt.ylabel('Loss', fontsize=12)
plt.legend(fontsize=10)
plt.grid(True, linestyle='--', alpha=0.7)

# 优化布局
plt.tight_layout()

# 保存图表
plt.savefig('./loss_comparison.png', dpi=300, bbox_inches='tight')
plt.close()
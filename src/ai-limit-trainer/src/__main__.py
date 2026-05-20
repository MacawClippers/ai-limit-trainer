"""
Entry point for ai-limit-trainer CLI.
"""

import sys
import numpy as np
from .limit_training import LimitTrainer


def example_function(x):
    """Example: limit of sin(x)/x as x->0 is 1."""
    if abs(x) < 1e-12:
        return 1.0
    return np.sin(x) / x


def main():
    """Run a demo of the limit trainer."""
    print("AI Limit Trainer Demo")
    print("=" * 40)
    print("Function: sin(x)/x, target x = 0")
    print("True limit: 1.0")
    print()

    trainer = LimitTrainer(degree=4, num_samples=2000, epsilon=0.5)
    trainer.train(example_function, target=0.0)
    prediction = trainer.predict_limit(0.0)
    score = trainer.score(example_function, 0.0)

    print(f"Predicted limit: {prediction:.6f}")
    print(f"R^2 score: {score:.6f}")
    print(f"Error: {abs(prediction - 1.0):.6f}")


if __name__ == "__main__":
    main()

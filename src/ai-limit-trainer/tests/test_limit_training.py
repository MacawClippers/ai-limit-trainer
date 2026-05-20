"""
Tests for the LimitTrainer class.
"""

import numpy as np
import pytest
from src.limit_training import LimitTrainer
from src.utils import numerical_limit, limit_epsilon_delta


def test_limit_trainer_linear():
    """Test with linear function (limit is trivial)."""
    def f(x):
        return 2*x + 3
    trainer = LimitTrainer(degree=1, num_samples=500, epsilon=0.2)
    trainer.train(f, target=1.0)
    pred = trainer.predict_limit(1.0)
    assert abs(pred - 5.0) < 0.1, f"Expected ~5.0, got {pred}"


def test_limit_trainer_sin_x_over_x():
    """Test with sin(x)/x limit at 0."""
    def f(x):
        if abs(x) < 1e-12:
            return 1.0
        return np.sin(x) / x
    trainer = LimitTrainer(degree=4, num_samples=1000, epsilon=0.3)
    trainer.train(f, target=0.0)
    pred = trainer.predict_limit(0.0)
    assert abs(pred - 1.0) < 0.1, f"Expected ~1.0, got {pred}"


def test_numerical_limit():
    """Test numerical limit utility."""
    def f(x):
        return np.sin(x) / x if abs(x) > 1e-12 else 1.0
    lim = numerical_limit(f, 0.0)
    assert abs(lim - 1.0) < 1e-4, f"Expected ~1.0, got {lim}"


def test_epsilon_delta():
    """Test epsilon-delta limit estimator."""
    def f(x):
        return x**2 + 1
    lim = limit_epsilon_delta(f, 2.0, epsilon=0.01)
    assert abs(lim - 5.0) < 0.1, f"Expected ~5.0, got {lim}"


def test_score_method():
    """Test that score returns a reasonable R^2."""
    def f(x):
        return 3*x - 2
    trainer = LimitTrainer(degree=1, num_samples=200, epsilon=0.2)
    trainer.train(f, target=0.0)
    score = trainer.score(f, 0.0)
    assert score > 0.8, f"R^2 too low: {score}"


def test_predict_without_train_raises():
    """Test that predicting without training raises error."""
    trainer = LimitTrainer()
    with pytest.raises(ValueError):
        trainer.predict_limit(0.0)

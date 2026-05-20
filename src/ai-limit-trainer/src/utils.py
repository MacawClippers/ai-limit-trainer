"""
Utility functions for limit approximation.
"""

import numpy as np


def numerical_limit(f, target: float, h: float = 1e-6) -> float:
    """
    Compute numerical limit using symmetric difference quotient.
    Useful as baseline comparison.

    Args:
        f: Function to evaluate.
        target: Point to approach.
        h: Step size (small).

    Returns:
        Approximate limit value.
    """
    if abs(f(target + h) - f(target - h)) < 1e-12:
        return f(target)
    return (f(target + h) + f(target - h)) / 2.0


def limit_epsilon_delta(f, target: float, epsilon: float = 0.01, max_iter: int = 1000) -> float:
    """
    Simple epsilon-delta search to estimate limit by averaging near target.

    Args:
        f: Function.
        target: Approach point.
        epsilon: Tolerance for y-variation.
        max_iter: Maximum iterations.

    Returns:
        Estimated limit.
    """
    delta = 1.0
    for _ in range(max_iter):
        x_vals = np.linspace(target - delta, target + delta, 100)
        y_vals = np.array([f(x) for x in x_vals])
        if np.max(y_vals) - np.min(y_vals) < epsilon:
            return float(np.mean(y_vals))
        delta *= 0.5
    return float(np.mean(y_vals))

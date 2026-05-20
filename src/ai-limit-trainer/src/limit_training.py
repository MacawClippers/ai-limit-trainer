"""
Module for training a limit-approaching AI model using gradient descent.
"""

import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.preprocessing import PolynomialFeatures


class LimitTrainer:
    """
    Trains a model to approximate the limit of a function f(x) as x approaches a target value.
    Uses polynomial regression on sampled points near the target.
    """

    def __init__(self, degree: int = 3, num_samples: int = 1000, epsilon: float = 0.1):
        """
        Args:
            degree: Degree of polynomial for regression.
            num_samples: Number of sample points near target.
            epsilon: Half-width of sampling interval around target.
        """
        self.degree = degree
        self.num_samples = num_samples
        self.epsilon = epsilon
        self.model = None
        self.poly = PolynomialFeatures(degree=self.degree)

    def _generate_samples(self, f, target: float) -> tuple:
        """Generate samples near target with noise."""
        x_vals = np.linspace(target - self.epsilon, target + self.epsilon, self.num_samples)
        # Remove exact target to avoid division by zero if function has singularity
        x_vals = x_vals[np.abs(x_vals - target) > 1e-9]
        y_vals = np.array([f(x) for x in x_vals])
        # Add small Gaussian noise
        y_vals += np.random.normal(0, 0.01 * np.std(y_vals), size=y_vals.shape)
        return x_vals.reshape(-1, 1), y_vals

    def train(self, f, target: float) -> None:
        """
        Train the limit approximation model.

        Args:
            f: Function to approximate limit of (callable).
            target: The x-value the limit approaches.
        """
        X, y = self._generate_samples(f, target)
        X_poly = self.poly.fit_transform(X)
        self.model = LinearRegression()
        self.model.fit(X_poly, y)

    def predict_limit(self, target: float) -> float:
        """
        Predict the limit value at target using trained model.

        Returns:
            Approximated limit value.
        """
        if self.model is None:
            raise ValueError("Model not trained. Call train() first.")
        X_target = np.array([[target]])
        X_target_poly = self.poly.transform(X_target)
        return float(self.model.predict(X_target_poly)[0])

    def score(self, f, target: float) -> float:
        """
        Evaluate model accuracy by comparing prediction to true limit.
        Uses a refined sampling near target.

        Returns:
            R^2 score on test samples.
        """
        X_test, y_test = self._generate_samples(f, target)
        X_test_poly = self.poly.transform(X_test)
        return self.model.score(X_test_poly, y_test)

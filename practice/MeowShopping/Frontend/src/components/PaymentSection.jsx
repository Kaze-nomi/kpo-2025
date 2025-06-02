import React, { useState } from 'react';
import api from '../services/api';

const PaymentSection = () => {
  const [amount, setAmount] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleDeposit = async (e) => {
    const userId = localStorage.getItem('userId');
    if (!userId) { return; }
    e.preventDefault();
    setIsLoading(true);
    try {
      const response = await api.payments.deposit(userId, parseFloat(amount));
      setMessage(response.data);
      setAmount('');
      setTimeout(() => setMessage(''), 3000);
    } catch (error) {
      setMessage(error.response?.data || 'Ошибка при пополнении счета');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <h2 className="section-title">Пополнение счета</h2>
      <form onSubmit={handleDeposit}>
        <div className="mb-4">
          <label className="block text-gray-700 mb-2">Сумма пополнения (₽):</label>
          <input
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            min="0.01"
            step="0.01"
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="Введите сумму"
            required
          />
        </div>
        <button
          type="submit"
          disabled={isLoading}
          className="btn-primary w-full disabled:opacity-50"
        >
          {isLoading ? (
            <div className="flex items-center justify-center">
              <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
              Обработка...
            </div>
          ) : (
            'Пополнить счет'
          )}
        </button>
      </form>
      {message && (
        <div className="mt-4 p-3 bg-green-100 text-green-700 rounded-lg animate-pulse">
          {message}
        </div>
      )}
    </div>
  );
};

export default PaymentSection;
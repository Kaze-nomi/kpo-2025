import React, { useState } from 'react';
import api from '../services/api';

const OrderSection = () => {
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleCreateOrder = async (e) => {
    const userId = localStorage.getItem('userId');
    if (!userId) { return; }
    e.preventDefault();
    setIsLoading(true);
    try {
      const response = await api.orders.createOrder(userId, parseFloat(amount), description);
      setMessage(response.data);
      setAmount('');
      setDescription('');
      setTimeout(() => setMessage(''), 3000);
    } catch (error) {
      setMessage(error.response?.data || 'Ошибка при создании заказа');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <h2 className="section-title">Создать заказ</h2>
      <form onSubmit={handleCreateOrder}>
        <div className="mb-4">
          <label className="block text-gray-700 mb-2">Сумма заказа (₽):</label>
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
        <div className="mb-4">
          <label className="block text-gray-700 mb-2">Описание заказа:</label>
          <input
            type="text"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="Например: Книга по микросервисам"
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
              Создание...
            </div>
          ) : (
            'Создать заказ'
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

export default OrderSection;
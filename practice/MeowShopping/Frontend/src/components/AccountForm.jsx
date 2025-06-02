import React, { useState } from 'react';
import api from '../services/api';

const AccountForm = ({ onLogin }) => {
  const [userId, setUserId] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const response = await api.payments.createAccount(userId);
      setMessage(response.data);
      onLogin(userId);
      setTimeout(() => setMessage(''), 3000);
    } catch (error) {
      setMessage(error.response?.data || 'Ошибка при создании счета');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-xl font-semibold mb-4 text-purple-700">Создание счета</h2>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-gray-700 mb-2">Ваш ID пользователя:</label>
          <input
            type="text"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
            placeholder="Введите ваш уникальный ID"
            required
          />
        </div>
        <button
          type="submit"
          disabled={isLoading}
          className="w-full bg-purple-600 hover:bg-purple-700 text-white font-medium py-2 px-4 rounded-lg transition duration-300 disabled:opacity-50"
        >
          {isLoading ? 'Создание...' : 'Создать счет'}
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

export default AccountForm;

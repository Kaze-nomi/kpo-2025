import React, { useState } from 'react';
import api from '../services/api';
import { useNotification } from './GlobalNotification';

const AccountForm = ({ onLogin }) => {
  const [userId, setUserId] = useState('');
  const [isLoginMode, setIsLoginMode] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const { showNotification } = useNotification();

  const validateUserId = (value) => {
    const num = Number(value);
    return num > 0 && num % 1 === 0 && num <= Math.pow(2, 31) - 1;
  };

  const handleUserIdChange = (e) => {
    const value = e.target.value;
    if (validateUserId(value)) {
      setUserId(value);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateUserId(userId)) {
      showNotification('ID должен быть положительным целым числом', 'error');
      return;
    }
    
    setIsLoading(true);
    try {
      if (isLoginMode) {
        var response = await api.payments.accountExists(userId);
        if (response.data === "Exists") {
          onLogin(userId);
          showNotification('Успешный вход!', 'success');
        } else {
          showNotification("Аккаунт не найден!", 'warning');
        }
      } else {
        await api.payments.createAccount(userId);
        onLogin(userId);
        showNotification('Счет успешно создан!', 'success');
      }
    } catch (error) {
      if (error.status === 500 && error.message === "Not exists") {
        showNotification("Аккаунт не найден!", 'warning');
      } else if (error.status === 500 && error.message === "Exists") {
        showNotification("Аккаунт уже существует!", 'warning');
      } else if (error.status === 500) {
        showNotification(error.message, 'error');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      <div className="flex mb-6 border-b">
        <button
          className={`flex-1 py-2 font-medium ${isLoginMode ? 'text-purple-700 border-b-2 border-purple-700' : 'text-gray-500'}`}
          onClick={() => setIsLoginMode(true)}
        >
          Вход
        </button>
        <button
          className={`flex-1 py-2 font-medium ${!isLoginMode ? 'text-purple-700 border-b-2 border-purple-700' : 'text-gray-500'}`}
          onClick={() => setIsLoginMode(false)}
        >
          Регистрация
        </button>
      </div>

      <h2 className="text-xl font-semibold mb-4 text-purple-700">
        {isLoginMode ? 'Войти в аккаунт' : 'Создать новый счёт'}
      </h2>
      
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-gray-700 mb-2">Ваш ID пользователя:</label>
          <input
            type="number"
            value={userId}
            onChange={handleUserIdChange}
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
          {isLoading ? (
            <div className="flex items-center justify-center">
              <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
              {isLoginMode ? 'Вход...' : 'Создание...'}
            </div>
          ) : (
            isLoginMode ? 'Войти' : 'Создать счет'
          )}
        </button>
      </form>
    </div>
  );
};

export default AccountForm;
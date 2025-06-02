import React, { useState, useEffect } from 'react';
import api from '../services/api';
import StatusBubble from './StatusBubble';

const OrderList = () => {
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchOrders = async () => {
    const userId = localStorage.getItem('userId');
    if (!userId) return;
    
    setIsLoading(true);
    try {
      const response = await api.orders.getOrders(userId);
      let ordersData = [];
      
      // Обработка разных форматов ответа
      if (Array.isArray(response.data)) {
        // JSON формат
        ordersData = response.data;
      } else if (typeof response.data === 'string') {
        // Текстовый формат
        ordersData = response.data
          .split('\n')
          .slice(1)
          .filter(line => line.trim() !== '')
          .map(line => {
            const match = line.match(/- ID: (\w+), Сумма: ([\d.]+), Статус: (\w+), Описание: (.+)$/);
            return match ? {
              id: match[1],
              amount: parseFloat(match[2]),
              status: match[3],
              description: match[4]
            } : null;
          })
          .filter(Boolean);
      }
      
      setOrders(ordersData);
      setError('');
    } catch (err) {
      setError(err.toString());
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold text-purple-700">История заказов</h2>
        <button 
          onClick={fetchOrders}
          className="text-indigo-600 hover:text-indigo-800 transition flex items-center"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clipRule="evenodd" />
          </svg>
          Обновить
        </button>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-8">
          <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-purple-600"></div>
        </div>
      ) : error ? (
        <div className="text-red-500 bg-red-50 p-3 rounded-lg">{error}</div>
      ) : orders.length === 0 ? (
        <p className="text-gray-600 py-4 text-center">У вас пока нет заказов</p>
      ) : (
        <div className="space-y-4">
          {orders.map(order => (
            <div key={order.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition">
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-medium text-lg">{order.description}</h3>
                  <p className="text-gray-600">ID: {order.id}</p>
                </div>
                <StatusBubble status={order.status} />
              </div>
              <div className="mt-3 flex justify-between items-center">
                <span className="text-gray-700">Сумма: <span className="font-semibold">{order.amount.toFixed(2)} ₽</span></span>
                <button 
                  onClick={() => window.location.href = `/order/${order.id}`}
                  className="text-indigo-600 hover:text-indigo-800 text-sm"
                >
                  Подробнее →
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default OrderList;
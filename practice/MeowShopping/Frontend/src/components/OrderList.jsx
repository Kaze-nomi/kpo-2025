import React, { useState, useEffect } from 'react';
import StatusBubble from './StatusBubble';

const OrderList = ({
  userId,
  orders,
  isLoading,
  fetchOrders
}) => {
  const [error, setError] = useState('');
  const [expandedOrderId, setExpandedOrderId] = useState(null);
  const [receiptData, setReceiptData] = useState(null);

  useEffect(() => {
    if (userId) {
      fetchOrders();
    }
  }, [userId, fetchOrders]);

  const toggleOrderDetails = (orderId) => {
    setExpandedOrderId(expandedOrderId === orderId ? null : orderId);
  };

  const generateReceipt = (order) => {
    const transactionId = `TRX-${simpleHash(userId + order.id)}`;
    const cardLast4 = getCardLast4(userId);

    const receipt = {
      id: order.id,
      date: new Date().toLocaleDateString('ru-RU'),
      time: new Date().toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }),
      amount: order.amount,
      description: order.description,
      status: order.status,
      paymentMethod: `ВТБ •••• ${cardLast4}`,
      transactionId: transactionId
    };
    setReceiptData(receipt);
  };

  const closeReceipt = () => {
    setReceiptData(null);
  };

  const printReceipt = () => {
    window.print();
  };

  const simpleHash = (str) => {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      hash = ((hash << 5) - hash) + str.charCodeAt(i);
      hash |= 0;
    }
    return Math.abs(hash).toString(16).padStart(8, '0').substring(0, 8);
  };

  const getCardLast4 = (userId) => {
    let hash = 0;
    for (let i = 0; i < userId.length; i++) {
      hash = (hash * 31 + userId.charCodeAt(i)) % 10000;
    }
    return hash.toString().padStart(4, '0');
  };

  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      {/* Модальное окно квитанции */}
      {receiptData && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md animate-fadeIn">
            <div className="p-6">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold text-purple-700">Квитанция заказа</h2>
                <button
                  onClick={closeReceipt}
                  className="text-gray-500 hover:text-gray-700"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div className="border border-dashed border-purple-300 rounded-lg p-4 mb-6 bg-purple-50">
                <div className="text-center mb-4">
                  <div className="text-2xl font-bold text-purple-800">Интернет-магазин НИУ ВШЭ</div>
                  <div className="text-gray-600 text-sm">Квитанция #{receiptData.id}</div>
                </div>

                <div className="space-y-3">
                  <div className="flex justify-between border-b pb-2">
                    <span className="text-gray-600">Дата:</span>
                    <span className="font-medium">{receiptData.date} в {receiptData.time}</span>
                  </div>

                  <div className="flex justify-between border-b pb-2">
                    <span className="text-gray-600">Транзакция:</span>
                    <span className="font-mono">{receiptData.transactionId}</span>
                  </div>

                  <div className="flex justify-between border-b pb-2">
                    <span className="text-gray-600">Метод оплаты:</span>
                    <span className="font-medium">{receiptData.paymentMethod}</span>
                  </div>

                  <div className="flex justify-between border-b pb-2">
                    <span className="text-gray-600">Описание:</span>
                    <span className="font-medium text-right">{receiptData.description}</span>
                  </div>

                  <div className="flex justify-between text-lg pt-2">
                    <span className="font-bold">Итого:</span>
                    <span className="font-bold text-purple-700">{receiptData.amount.toFixed(2)} ₽</span>
                  </div>
                </div>

                <div className="mt-6 text-center text-xs text-gray-500">
                  <p>Спасибо за покупку!</p>
                  <p>По всем вопросам: @kazenomi</p>
                </div>
              </div>

              <div className="flex space-x-3">
                <button
                  onClick={printReceipt}
                  className="flex-1 bg-purple-600 hover:bg-purple-700 text-white py-3 px-4 rounded-lg font-medium transition flex items-center justify-center"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                  </svg>
                  Печать
                </button>
                <button
                  onClick={closeReceipt}
                  className="flex-1 bg-white border border-gray-300 hover:bg-gray-100 text-gray-700 py-3 px-4 rounded-lg font-medium transition"
                >
                  Закрыть
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

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
        <div className="space-y-3">
          {orders.map(order => (
            <div
              key={order.id}
              className={`border border-gray-200 rounded-lg overflow-hidden transition-all duration-300 ${expandedOrderId === order.id ? 'shadow-lg bg-purple-50' : 'hover:shadow-md'
                }`}
            >
              <div
                className={`p-4 cursor-pointer flex justify-between items-center ${expandedOrderId === order.id ? 'bg-purple-100' : ''
                  }`}
                onClick={() => toggleOrderDetails(order.id)}
              >
                <div>
                  <h3 className="font-medium text-lg flex items-center">
                    {order.description}
                    <span className="ml-2">
                      <StatusBubble status={order.status} />
                    </span>
                  </h3>
                  <p className="text-gray-500 text-sm mt-1">
                    {order.createdAt}
                  </p>
                </div>
                <div className="flex items-center">
                  <span className="text-gray-700 mr-4">
                    {order.amount.toFixed(2)} ₽
                  </span>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className={`h-5 w-5 text-purple-600 transform transition-transform duration-300 ${expandedOrderId === order.id ? 'rotate-180' : ''
                      }`}
                    viewBox="0 0 20 20"
                    fill="currentColor"
                  >
                    <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
                  </svg>
                </div>
              </div>

              {expandedOrderId === order.id && (
                <div className="animate-fadeIn px-4 pb-4 pt-2 border-t border-gray-200">
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <p className="text-gray-500">Дата создания</p>
                      <p className="font-medium">{order.createdAt || 'Неизвестно'}</p>
                    </div>
                    <div>
                      <p className="text-gray-500">Последнее обновление</p>
                      <p className="font-medium">{order.updatedAt || 'Неизвестно'}</p>
                    </div>
                    <div>
                      <p className="text-gray-500">Сумма заказа</p>
                      <p className="font-medium text-lg text-purple-700">{order.amount.toFixed(2)} ₽</p>
                    </div>
                    <div>
                      <p className="text-gray-500">Статус</p>
                      <div className="mt-1">
                        <StatusBubble status={order.status} />
                      </div>
                    </div>
                  </div>

                  <div className="mt-4">
                    <p className="text-gray-500">Описание</p>
                    <p className="font-medium mt-1 bg-white p-3 rounded-lg border border-gray-200">
                      {order.description}
                    </p>
                  </div>

                  <div className="mt-4">
                    {order.status === 'FINISHED' && (
                      <button
                        onClick={() => generateReceipt(order)}
                        className="w-full bg-purple-600 hover:bg-purple-700 text-white py-2 px-4 rounded-lg font-medium transition flex items-center justify-center"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                        Получить квитанцию
                      </button>
                    )}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default OrderList;


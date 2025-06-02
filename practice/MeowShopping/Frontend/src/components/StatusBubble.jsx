import React from 'react';

const StatusBubble = ({ status }) => {
  // Приводим статус к нижнему регистру для унификации
  const statusLower = status.toLowerCase();
  
  let bgColor = 'bg-gray-200';
  let textColor = 'text-gray-800';
  
  if (statusLower.includes('created') || statusLower.includes('создан')) {
    bgColor = 'bg-blue-100';
    textColor = 'text-blue-800';
  } else if (statusLower.includes('processing') || statusLower.includes('обработк')) {
    bgColor = 'bg-yellow-100';
    textColor = 'text-yellow-800';
  } else if (statusLower.includes('completed') || statusLower.includes('завершен')) {
    bgColor = 'bg-green-100';
    textColor = 'text-green-800';
  } else if (statusLower.includes('cancelled') || statusLower.includes('отменен')) {
    bgColor = 'bg-red-100';
    textColor = 'text-red-800';
  }

  // Красивое отображение статуса
  const getDisplayStatus = () => {
    if (statusLower.includes('created')) return 'Создан';
    if (statusLower.includes('processing')) return 'В обработке';
    if (statusLower.includes('completed')) return 'Завершен';
    if (statusLower.includes('cancelled')) return 'Отменен';
    return status;
  };

  return (
    <span className={`px-3 py-1 rounded-full text-sm font-medium ${bgColor} ${textColor} transition-all duration-300`}>
      {getDisplayStatus()}
    </span>
  );
};

export default StatusBubble;
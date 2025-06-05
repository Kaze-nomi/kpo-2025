import React from 'react';

const StatusBubble = ({ status }) => {
  const statusLower = status.toLowerCase();
  
  let bgColor = 'bg-gray-200';
  let textColor = 'text-gray-800';
  
  if (statusLower.includes('new')) {
    bgColor = 'bg-blue-100';
    textColor = 'text-blue-800';
  } else if (statusLower.includes('finished')) {
    bgColor = 'bg-green-100';
    textColor = 'text-green-800';
  } else if (statusLower.includes('canceled')) {
    bgColor = 'bg-red-100';
    textColor = 'text-red-800';
  }

  const getDisplayStatus = () => {
    if (statusLower.includes('new')) return 'В обработке';
    if (statusLower.includes('finished')) return 'Завершен';
    if (statusLower.includes('canceled')) return 'Отменен';
    return status;
  };

  return (
    <span className={`px-3 py-1 rounded-full text-sm font-medium ${bgColor} ${textColor} transition-all duration-300`}>
      {getDisplayStatus()}
    </span>
  );
};

export default StatusBubble;
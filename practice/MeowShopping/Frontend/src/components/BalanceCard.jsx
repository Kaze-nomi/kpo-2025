import React, { useState, useEffect } from 'react';
import api from '../services/api';

const BalanceCard = ({ userId }) => {
    const [balance, setBalance] = useState(null);
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(true);

    const fetchBalance = async () => {
        if (!userId) return;
        
        setIsLoading(true);
        setError('');
        const response = await api.payments.getBalance(userId);
        setBalance(response.data);
        setIsLoading(false);
    }
    
    useEffect(() => {
        fetchBalance();
    });

    return (
        <div className="bg-gradient-to-br from-blue-50 to-indigo-100 p-6 rounded-xl shadow-md">
            <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-semibold text-indigo-700">Баланс счета</h2>
            </div>
            
            {isLoading ? (
            <div className="flex items-center justify-center text-indigo-700">
              <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-current mr-2"></div>
              Обработка...
            </div>
            ) : error ? (
                <div className="text-red-500 bg-red-50 p-3 rounded-lg">{error}</div>
            ) : balance ? (
                <div className="text-center">
                    <div className="text-4xl font-bold text-indigo-800 mb-2">
                        {balance.split(':')[1].trim()} ₽
                    </div>
                </div>
            ) : (
                <p className="text-gray-600">Создайте счет для просмотра баланса</p>
            )}
        </div>
    );
};

export default BalanceCard;
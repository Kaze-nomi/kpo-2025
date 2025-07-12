import React  from 'react';

const BalanceCard = ({ userId, balance, isLoading }) => {
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
            ) :
                <div className="text-center">
                    <div className="text-4xl font-bold text-indigo-800 mb-2">
                        {balance} ₽
                    </div>
                </div>
            }
        </div>
    );
};

export default BalanceCard;
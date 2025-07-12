import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const ErrorPage = () => {
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.removeItem('userId');
  }, []);

  const handleGoHome = () => {
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center items-center px-4">
      <div className="max-w-md w-full bg-white p-8 rounded-xl shadow-md text-center">
        <div className="text-6xl mb-6">😿</div>
        <h1 className="text-2xl font-bold text-gray-800 mb-4">Упс! Что-то пошло не так</h1>
        <p className="text-gray-600 mb-6">
          К сожалению, ваш аккаунт был удалён. 
          Приносим извинения за доставленные неудобства.
          По всем вопросам пишите в телеграмм технического директора магазина @kazenomi. (возврат денег не осуществляем)
        </p>
        <button
          onClick={handleGoHome}
          className="bg-purple-600 hover:bg-purple-700 text-white font-medium py-2 px-4 rounded-lg transition duration-300"
        >
          Вернуться на главную
        </button>
      </div>
    </div>
  );
};

export default ErrorPage;
import React from 'react';

const Header = () => {

  const handleGoHome = () => {
    localStorage.removeItem('userId');
    window.location.reload();
  };

  return (
    <header className="bg-gradient-to-r from-purple-600 to-indigo-700 text-white p-4 shadow-lg">
      <div className="container mx-auto flex justify-between items-center">
        <h1 className="text-2xl font-bold flex items-center">
          <span className="mr-2">🐱</span> 
          Интернет-магазин НИУ ВШЭ
        </h1>
        <div className="flex items-center">
          {!localStorage.getItem('userId') ? (
            <span className="bg-yellow-100 text-purple-800 px-3 py-1 rounded-full text-sm font-medium">
              Не авторизован
            </span>
          ) : (
            <>
              <span className="bg-yellow-100 text-purple-800 px-3 py-1 rounded-full text-sm font-medium">
                Ваш ID: {localStorage.getItem('userId')}
              </span>
              <button
                type="button"
                onClick={handleGoHome}
                className="bg-red-600 hover:bg-red-700 text-white ml-4 py-1 px-3 rounded-lg transition duration-300"
              >
                Выйти
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;

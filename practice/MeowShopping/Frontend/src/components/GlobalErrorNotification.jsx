    import React, { createContext, useContext, useState, useCallback } from 'react';

    const ErrorContext = createContext();

    export const ErrorProvider = ({ children }) => {
    const [errors, setErrors] = useState([]);

    const showError = useCallback((message, status = 'error') => {
        const id = Date.now();
        setErrors(prev => [...prev, { id, message, status }]);
        
        // Автоматическое скрытие через 5 секунд
        setTimeout(() => {
        setErrors(prev => prev.filter(error => error.id !== id));
        }, 5000);
    }, []);

    const removeError = useCallback((id) => {
        setErrors(prev => prev.filter(error => error.id !== id));
    }, []);

    return (
        <ErrorContext.Provider value={{ showError }}>
        {children}
        <div className="fixed top-4 right-4 z-50 space-y-3">
            {errors.map(error => (
            <div 
                key={error.id}
                className={`min-w-64 max-w-md p-4 rounded-lg shadow-lg animate-fadeIn ${
                error.status === 'error' 
                    ? 'bg-red-100 border-l-4 border-red-500 text-red-700'
                    : 'bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700'
                }`}
            >
                <div className="flex justify-between items-start">
                <div>
                    <p className="font-medium">{error.status === 'error' ? 'Что-то сломалось!' : 'Внимание'}</p>
                    <p>{error.message}</p>
                </div>
                <button 
                    onClick={() => removeError(error.id)}
                    className="ml-4 text-gray-500 hover:text-gray-700"
                >
                </button>
                </div>
            </div>
            ))}
        </div>
        </ErrorContext.Provider>
    );
    };

    export const useError = () => {
    const context = useContext(ErrorContext);
    if (!context) {
        throw new Error('useError must be used within an ErrorProvider');
    }
    return context;
    };

    export default ErrorProvider;
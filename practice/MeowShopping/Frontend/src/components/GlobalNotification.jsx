import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';

const NotificationContext = createContext();
const MAX_NOTIFICATIONS = 3;
const DEFAULT_DURATION = 5000; // 5 секунд
const ANIMATION_DURATION = 300; // Длительность анимации

export const NotificationProvider = ({ children }) => {
    const [notifications, setNotifications] = useState([]);

    // Ограничение количества уведомлений
    useEffect(() => {
        if (notifications.length > MAX_NOTIFICATIONS) {
            setNotifications(prev => prev.slice(prev.length - MAX_NOTIFICATIONS));
        }
    }, [notifications]);

    const showNotification = useCallback((message, status = 'error', duration = DEFAULT_DURATION) => {
        const id = Date.now();
        setNotifications(prev => [...prev, { id, message, status, duration }]);
    }, []);

    const removeNotification = useCallback((id) => {
        setNotifications(prev => prev.filter(notification => notification.id !== id));
    }, []);

    return (
        <NotificationContext.Provider value={{ showNotification }}>
            {children}
            <div className="fixed top-4 right-4 z-50 space-y-3">
                {notifications.map(notification => (
                    <NotificationItem 
                        key={notification.id}
                        notification={notification}
                        onClose={removeNotification}
                    />
                ))}
            </div>
        </NotificationContext.Provider>
    );
};

const NotificationItem = React.memo(({ notification, onClose }) => {
    const { id, message, status, duration } = notification;
    const [isClosing, setIsClosing] = useState(false);
    
    // Запуск автоматического закрытия
    useEffect(() => {
        if (duration === 0) return; // Для бессрочных уведомлений
        
        const timer = setTimeout(() => {
            startClosing();
        }, duration);

        return () => clearTimeout(timer);
    }, [duration]);

    // Запуск анимации закрытия
    const startClosing = () => {
        setIsClosing(true);
        setTimeout(() => {
            onClose(id);
        }, ANIMATION_DURATION);
    };

    // Определение классов для анимации
    const getAnimationClass = () => {
        if (isClosing) return 'animate-fadeOut';
        return 'animate-fadeIn';
    };

    return (
        <div
            className={`min-w-64 max-w-md p-4 rounded-lg shadow-lg ${getAnimationClass()} ${
                status === 'error'
                    ? 'bg-red-100 border-l-4 border-red-500 text-red-700'
                    : status === 'success'
                    ? 'bg-green-100 border-l-4 border-green-500 text-green-700'
                    : 'bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700'
            }`}
            style={{ animationDuration: `${ANIMATION_DURATION}ms` }}
        >
            <div className="flex justify-between items-start">
                <div>
                    <p className="font-medium">
                        {status === 'error' 
                            ? 'Что-то сломалось!' 
                            : status === 'success' 
                                ? 'Успешно' 
                                : 'Внимание'}
                    </p>
                    <p>{message}</p>
                </div>
                <button
                    onClick={startClosing}
                    className="ml-4 text-gray-500 hover:text-gray-700"
                    aria-label="Закрыть уведомление"
                >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                    </svg>
                </button>
            </div>
        </div>
    );
});

export const useNotification = () => {
    const context = useContext(NotificationContext);
    if (!context) {
        throw new Error('useNotification must be used within an NotificationProvider');
    }
    return context;
};

export default NotificationProvider;
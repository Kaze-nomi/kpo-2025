import { useState, useCallback } from 'react';
import api from '../services/api';
import { useNotification } from './GlobalNotification';

export const useOrders = (userId) => {
    const [orders, setOrders] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const { showNotification } = useNotification();

    const fetchOrders = useCallback(async () => {
        if (!userId) return;
        
        setIsLoading(true);
        try {
            const response = await api.orders.getOrders(userId);
            let ordersData = [];
            
            if (Array.isArray(response.data)) {
                ordersData = response.data;
            } else if (typeof response.data === 'string') {
                ordersData = response.data
                    .split('\n')
                    .slice(1)
                    .filter(line => line.trim() !== '')
                    .map(line => {
                        const match = line.match(/- ID: (\w+), Сумма: ([\d.]+), Статус: (\w+), Описание: (.+)$/);
                        if (match) {
                            const dateOptions = {
                                day: '2-digit',
                                month: 'long',
                                year: 'numeric',
                                hour: '2-digit',
                                minute: '2-digit'
                            };
                            
                            return {
                                id: match[1],
                                amount: parseFloat(match[2]),
                                status: match[3],
                                description: match[4],
                                createdAt: new Date().toLocaleString('ru-RU', dateOptions),
                                updatedAt: new Date().toLocaleString('ru-RU', dateOptions)
                            };
                        }
                        return null;
                    })
                    .filter(Boolean);
            }
            
            setOrders(ordersData);
            setError('');
        } catch (err) {
            if (err.status === 500 && !err.isAccountError) {
                showNotification(err.message, 'error');
            }
        } finally {
            setIsLoading(false);
        }
    }, [userId, showNotification]);

    return { orders, isLoading, error, fetchOrders };
};
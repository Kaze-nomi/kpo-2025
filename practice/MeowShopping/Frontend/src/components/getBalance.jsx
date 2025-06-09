import { useState, useCallback } from 'react';
import api from '../services/api';

export const useBalance = (userId) => {
    const [balance, setBalance] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    const fetchBalance = useCallback(async () => {

        var error = false;

        setIsLoading(true);

        if (!userId) {
            return;
        }

        try {
            const response = await api.payments.getBalance(userId);
            const balanceText = response.data;
            const match = balanceText.match(/Текущий баланс:\s*([-+]?\d*\.?\d+(?:[eE][-+]?\d+)?)/);
            var balanceValue = 0;
            if (match) {
                balanceValue = parseFloat(match[1]);
            }
            setBalance(balanceValue.toLocaleString('ru-RU'));
        } catch (err) {
            error = true;
        }
        finally {
            if (!error) {
                setIsLoading(false);
            }
        }
    }, [userId]);

    return { balance, isLoading, fetchBalance };
};
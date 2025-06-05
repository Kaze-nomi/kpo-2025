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
            const balanceValue = response.data;
            const match = balanceValue.match(/[\d,]+\.?\d*/);
            if (match) {
                setBalance(match[0]);
            }
        } catch {
            error = true;
        } finally {
            if (!error) {
                setIsLoading(false);
            }
        }
    }, [userId]);

    return { balance, isLoading, fetchBalance };
};
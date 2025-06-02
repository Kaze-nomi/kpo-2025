import axios from 'axios';

// Создаем экземпляр axios без хуков
const api = axios.create({
  baseURL: '/api/shopping',
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000,
});

// Функция для установки хуков после создания экземпляра
let navigateHandler;
let showErrorHandler;
let setUserIdHandler;

export const setupAxiosInterceptors = (navigate, showError, setUserId) => {
  navigateHandler = navigate;
  showErrorHandler = showError;
  setUserIdHandler = setUserId;
};

api.interceptors.response.use(
  response => response,
  error => {
    // Формируем стандартизированный объект ошибки
    let errorObj = {
      message: 'Неизвестная ошибка сервера',
      status: 999,
      original: error,
      isAccountError: false
    };

    if (error.response) {
      // Обработка специфических статусов
      errorObj.status = error.response.status;

      if (error.response.status === 503) {
        errorObj.message = 'Сервис временно недоступен. Пожалуйста, попробуйте позже';
      } else if (error.response.status === 404) {
        errorObj.message = 'Ресурс не найден';
      } else if (error.response.status === 400) {
        errorObj.message = 'Неверный запрос. Пожалуйста, проверьте переданные данные';
      } else if (error.response.data) {
        errorObj.status = error.response?.status || 999;
        // Извлекаем сообщение из ответа
        errorObj.message = typeof error.response.data === 'string'
          ? error.response.data
          : error.response.data.message || JSON.stringify(error.response.data);
      }
    } else if (error.message) {
      errorObj.message = error.message;
    } else if (error.request) {
      errorObj.message = 'Сервер не ответил';
    }

    const accountErrors = [
      'Аккаунт не найден'
    ];

    errorObj.isAccountError = accountErrors.some(term =>
      errorObj.message.toLowerCase().includes(term.toLowerCase())
    );

    if (errorObj.isAccountError) {
      if (navigateHandler && setUserIdHandler) {
        localStorage.removeItem('userId');
        setUserIdHandler('');
        navigateHandler('/error');
      }
      return Promise.reject(errorObj);
    }

    if (showErrorHandler) {
      if (errorObj.status === 503 || errorObj.status === 502) {
        showErrorHandler('Сервис сейчас спит, попробуйте позже! 💤', 'warning');
      } else if (errorObj.status < 500 && errorObj.status >= 400) {
        showErrorHandler("Ошибка клиента: " + (errorObj.message || 'Неизвестная ошибка'), 'error');
      } else if (errorObj.status !== 500) {
        showErrorHandler("Ошибка: " + (errorObj.message || errorObj.status || 'Неизвестная ошибка'), 'error');
      }
    }

    return Promise.reject(errorObj);
  }
);

export default {
  payments: {
    createAccount: (userId) => api.post('/payments/account', null, { headers: { 'user-id': userId } }),
    deposit: (userId, amount) => api.post('/payments/deposit', null, { headers: { 'user-id': userId }, params: { amount } }),
    getBalance: (userId) => api.get('/payments/balance', { headers: { 'user-id': userId } })
  },
  orders: {
    createOrder: (userId, amount, description) => api.post('/orders', null, { headers: { 'user-id': userId }, params: { amount, description } }),
    getOrders: (userId) => api.get('/orders', { headers: { 'user-id': userId } }),
    getOrderStatus: (orderId) => api.get(`/orders/${orderId}`)
  },
  setupInterceptors: setupAxiosInterceptors
};
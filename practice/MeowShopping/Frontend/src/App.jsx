import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';
import Header from './components/Header';
import AccountForm from './components/AccountForm';
import BalanceCard from './components/BalanceCard';
import OrderSection from './components/OrderSection';
import PaymentSection from './components/PaymentSection';
import OrderList from './components/OrderList';
import ErrorPage from './components/ErrorPage';
import Footer from './components/Footer';
import { NotificationProvider, useNotification } from './components/GlobalNotification';
import api from './services/api';
import { useBalance } from './components/getBalance';
import { useOrders } from './components/getOrders';
import { WebSocketProvider, useWebSocket } from './components/WebSocketContext';


const MainApp = () => {
    const [activeTab, setActiveTab] = React.useState('payments');
    const [userId, setUserId] = React.useState(() => localStorage.getItem('userId') || '');
    const navigate = useNavigate();
    const { showNotification } = useNotification();
    const { connect } = useWebSocket();


    api.setupInterceptors(navigate, showNotification, setUserId);

    const { balance, isLoading: isBalanceLoading, fetchBalance } = useBalance(userId);
    const { orders, isLoading: isOrdersLoading, fetchOrders } = useOrders(userId);

    React.useEffect(() => {
        if (!userId) return;
        fetchBalance().catch((error) => {
            if (error.status === 500 && !error.isAccountError) {
                showNotification(error.message, 'error');
            }
        });
        connect(userId, handleWebSocketUpdate);
    }, [userId, connect, fetchBalance, showNotification]);

    const handleWebSocketUpdate = (orderUpdate) => {
        fetchOrders();
        if (orderUpdate.status === "FINISHED") {
            showNotification("Ваш заказ с ID " + orderUpdate.id + " \"" + orderUpdate.description + "\" завершён! :)", 'success', 10000);
            fetchBalance();
        } else if (orderUpdate.status === "CANCELLED") {
            showNotification("Ваш заказ с ID " + orderUpdate.id + " \"" + orderUpdate.description + "\" был отменён! :(", 'warning', 10000);
        } else {
            showNotification("Ваш заказ с ID " + orderUpdate.id + " \"" + orderUpdate.description + "\" находится в обработке!", 'success', 10000);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('userId');
        setUserId('');
        navigate('/');
    };

    return (
        <div className="flex flex-col min-h-screen bg-gray-50">
            <Header onLogout={handleLogout} userId={userId} />

            <main className="container mx-auto px-4 py-8 flex-grow">
                {!userId ? (
                    <div className="max-w-md mx-auto">
                        <AccountForm onLogin={(id) => {
                            localStorage.setItem('userId', id);
                            setUserId(id);
                        }} />
                    </div>
                ) : (
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                        <div className="lg:col-span-1 space-y-6">
                            <BalanceCard userId={userId} balance={balance} isLoading={isBalanceLoading} />

                            <div className="bg-white p-6 rounded-xl shadow-md">
                                <h2 className="text-xl font-semibold mb-4 text-purple-700">Быстрые действия</h2>
                                <div className="grid grid-cols-2 gap-4">
                                    <button
                                        onClick={() => setActiveTab('payments')}
                                        className={`${activeTab === 'payments' ? 'bg-indigo-200' : 'bg-indigo-100'} hover:bg-indigo-200 text-indigo-700 py-3 px-4 rounded-lg transition flex flex-col items-center`}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                        </svg>
                                        Платежи
                                    </button>
                                    <button
                                        onClick={() => setActiveTab('orders')}
                                        className={`${activeTab === 'orders' ? 'bg-purple-200' : 'bg-purple-100'} hover:bg-purple-200 text-purple-700 py-3 px-4 rounded-lg transition flex flex-col items-center`}
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 mb-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                                        </svg>
                                        Заказы
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div className="lg:col-span-2">
                            {activeTab === 'payments' ? (
                                <PaymentSection userId={userId} onDepositSuccess={fetchBalance} />
                            ) : null}
                            {activeTab === 'orders' ? (
                                <div className="space-y-8">
                                    <OrderSection userId={userId} onOrderCreated={() => {
                                        fetchOrders();
                                        fetchBalance();
                                    }} />
                                    <OrderList
                                        userId={userId}
                                        orders={orders}
                                        isLoading={isOrdersLoading}
                                        fetchOrders={fetchOrders}
                                    />
                                </div>
                            ) : null}
                        </div>
                    </div>
                )}
            </main>
            <Footer />
        </div>
    );
};

const App = () => {
    return (
        <WebSocketProvider>
            <NotificationProvider>
                <Router>
                    <Routes>
                        <Route path="/error" element={<ErrorPage />} />
                        <Route path="*" element={<MainApp />} />
                    </Routes>
                </Router>
            </NotificationProvider>
        </WebSocketProvider>
    );
};

export default App;

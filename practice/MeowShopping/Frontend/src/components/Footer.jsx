import React from 'react';

const Footer = () => (
  <footer className="mt-auto bg-gray-800 text-white py-6">
    <div className="container mx-auto px-4 text-center">
      <p>© {new Date().getFullYear()} Интернет-магазин НИУ ВШЭ. Все права защищены (котиками).</p>
    </div>
  </footer>
);

export default Footer;
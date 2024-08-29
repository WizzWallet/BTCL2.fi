/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#EBC28E',
        secondary: '#FFF0DD',
        success: '#38c172',
        danger: '#e3342f',
        'black-0': '#0b0b0f',
        'black-100': '#1D2129',
      },
    },
  },
  plugins: [],
};

import Model from './Model.js';
import View from './View.js';
import Controller from './Controller.js';

// Wait for DOM to be ready before initializing
document.addEventListener('DOMContentLoaded', () => {
    const app = new Controller(new Model(), new View());
    console.log("CardsMVC Initialized"); // Check your browser console for this!
});
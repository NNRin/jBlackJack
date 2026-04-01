import Model from './Model.js';
import View from './View.js';
import Controller from './Controller.js';

// wait for DOM to be ready before initializing app
document.addEventListener('DOMContentLoaded', () => {
    const app = new Controller(new Model(), new View());
    console.log("CardsMVC created");
});
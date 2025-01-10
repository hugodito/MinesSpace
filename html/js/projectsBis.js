document.addEventListener('DOMContentLoaded', loadProjects);

async function loadProjects() {
    const mainContent = document.getElementById('main-content');

    try {
        // Original path commented out for testing
        // const response = await fetch('/api/data/');
        const response = await fetch('resources/mockDb.json');
        const projects = await response.json();

        // Sort projects by launch_id from highest to lowest
        projects.sort((a, b) => b.launch_id - a.launch_id);

        // Use a Set to keep track of unique launch_ids
        const uniqueLaunchIds = new Set();

        projects.forEach(project => {
            if (!uniqueLaunchIds.has(project.launch_id)) {
                uniqueLaunchIds.add(project.launch_id);

                const projectBox = document.createElement('div');
                projectBox.classList.add('project-box');

                // Format the timestamp as a date
                const launchDate = new Date(project.timestamp * 1000).toLocaleDateString();

                projectBox.innerHTML = `
                    <h3>Projet n°${project.launch_id}</h3>
                    <p>${launchDate}</p>
                `;

                projectBox.addEventListener('click', () => {
                    // Check if the canvas already exists
                    let canvas = projectBox.querySelector('canvas');
                    if (!canvas) {
                        // Remove any existing canvas from other boxes
                        const existingCanvas = document.querySelector('canvas');
                        if (existingCanvas) {
                            existingCanvas.remove();
                            const existingButtons = document.querySelectorAll('.project-button');
                            existingButtons.forEach(button => button.remove());
                        }

                        // Create and append new canvas
                        canvas = document.createElement('canvas');
                        canvas.id = `canvas-${project.launch_id}`;
                        canvas.width = 400; // Set canvas width
                        canvas.height = 300; // Set canvas height
                        projectBox.appendChild(canvas);

                        // Load the canvas script
                        const script = document.createElement('script');
                        script.src = 'js/canvasBis.js';
                        script.onload = () => {
                            // Initialize the canvas drawing
                            initializeCanvas(canvas.id, project.launch_id);
                        };
                        document.body.appendChild(script);

                        // Create and append buttons
                        const buttonContainer = document.createElement('div');
                        buttonContainer.classList.add('button-container');

                        const button1 = document.createElement('button');
                        button1.classList.add('project-button');
                        button1.textContent = 'Altitude';
                        button1.addEventListener('click', () => updateGraph('altitude', 'timestamp', project.launch_id));
                        buttonContainer.appendChild(button1);

                        const button2 = document.createElement('button');
                        button2.classList.add('project-button');
                        button2.textContent = 'Temperature';
                        button2.addEventListener('click', () => updateGraph('temperature', 'timestamp', project.launch_id));
                        buttonContainer.appendChild(button2);

                        const button3 = document.createElement('button');
                        button3.classList.add('project-button');
                        button3.textContent = 'Speed';
                        button3.addEventListener('click', () => updateGraph('vitesse', 'timestamp', project.launch_id));
                        buttonContainer.appendChild(button3);

                        projectBox.appendChild(buttonContainer);
                    }
                });

                mainContent.appendChild(projectBox);
            }
        });
    } catch (error) {
        console.error('Error fetching projects:', error);
    }
}
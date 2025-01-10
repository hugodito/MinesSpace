async function initializeCanvas(canvasId, launchId) {
    const canvas = document.getElementById(canvasId);
    if (canvas.getContext) {
        const ctx = canvas.getContext('2d');

        // Set maximum size
        const maxWidth = 600;
        const maxHeight = 400;

        // Get the available width of the parent element
        const parentWidth = canvas.parentElement.clientWidth;

        // Set canvas size based on available space, but do not exceed the maximum size
        canvas.width = Math.min(parentWidth, maxWidth);
        canvas.height = Math.min(canvas.width * (maxHeight / maxWidth), maxHeight);

        // Set canvas styles
        canvas.style.backgroundColor = 'white';
        canvas.style.border = '2px solid black'; // Slightly larger border
        canvas.style.display = 'block';
        canvas.style.margin = '0 auto';

        try {
            // Fetch the data for the corresponding launch_id
            const response = await fetch('resources/mockDb.json');
            const data = await response.json();

            // Filter data for the specific launch_id
            const launchData = data.filter(entry => entry.launch_id === launchId);

            // Store the data for later use
            canvas.data = launchData;

            // Draw the initial graph (e.g., temperature vs. timestamp)
            updateGraph('temperature', 'timestamp', launchId);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    } else {
        console.error('Canvas not supported in this browser.');
    }
}

function updateGraph(yValueName, xValueName = 'timestamp', launchId) {
    const canvas = document.getElementById(`canvas-${launchId}`);
    if (canvas && canvas.getContext) {
        const ctx = canvas.getContext('2d');
        const data = canvas.data;

        // Clear the canvas
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // Extract x and y values
        const xValues = data.map(entry => new Date(entry[xValueName] * 1000));
        const yValues = data.map(entry => entry[yValueName]);

        // Find the min and max values for scaling
        const minX = Math.min(...xValues);
        const maxX = Math.max(...xValues);
        const minY = Math.min(...yValues);
        const maxY = Math.max(...yValues);

        // Normalize x values by subtracting the minimum x value
        const normalizedXValues = xValues.map(x => x - minX);

        // Scale the data to fit the canvas
        const scaleX = canvas.width / (maxX - minX);
        const scaleY = canvas.height / (maxY - minY);

        // Draw the grey horizontal line for the 0 y-value
        const zeroY = canvas.height - (0 - minY) * scaleY;
        ctx.beginPath();
        ctx.moveTo(0, zeroY);
        ctx.lineTo(canvas.width, zeroY);
        ctx.strokeStyle = 'lightgrey'; // More discrete grey line
        ctx.lineWidth = 1; // Thinner grey line
        ctx.stroke();

        // Draw the graph
        ctx.beginPath();
        ctx.moveTo(normalizedXValues[0] * scaleX, canvas.height - (yValues[0] - minY) * scaleY);
        for (let i = 1; i < normalizedXValues.length; i++) {
            ctx.lineTo(normalizedXValues[i] * scaleX, canvas.height - (yValues[i] - minY) * scaleY);
        }
        ctx.strokeStyle = 'red';
        ctx.lineWidth = 2; // Slightly larger graph line
        ctx.stroke();

        // Draw extremal values as plain text on the left
        ctx.fillStyle = 'black';
        ctx.font = '12px Arial';
        const unit = yValueName === 'temperature' ? '°C' : yValueName === 'altitude' ? 'm' : 'm/s';
        ctx.fillText(`Max: ${maxY} ${unit}`, 10, 20);
        ctx.fillText(`Min: ${minY} ${unit}`, 10, canvas.height - 10);
    } else {
        console.error('Canvas not supported in this browser.');
    }
}
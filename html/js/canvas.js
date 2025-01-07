let canvas = document.getElementById("canvas");
let context = canvas.getContext("2d");

var window_height = 300;
var window_width = 500;

canvas.width = window_width;
canvas.height = window_height;

canvas.style.background = "#ddf";

canvas.style.border = '1px solid #000';

/**
 * Fonction pour récupérer les données depuis l'API
 * 
 * @param {*} launchId Launch (project) id
 * @param {*} dataNameToPlotAsX x-axis name
 * @param {*} dataNameToPlotAsY y-axis name
 */
async function fetchDataAndPlot(launchId, dataNameToPlotAsX, dataNameToPlotAsY) {
    try {
        const response = await fetch(`./resources/mockDb.json`); // Remplacez par l'URL réelle de l'API
        if (!response.ok) {
            throw new Error(`Erreur API: ${response.statusText}`);
        }
        const apiData = await response.json();

        // Filtrer les données pour ne garder que celles correspondant au launchId
        const filteredData = apiData.filter(point => point.launch_id === launchId);

        // Extraire les données pour les axes X et Y
        const rawX = filteredData.map(point => point[dataNameToPlotAsX]);
        const rawY = filteredData.map(point => point[dataNameToPlotAsY]);

        const result = sortTwoLists(rawX, rawY);

        // Dessiner les données
        plotData(result.sortedL, result.sortedM);
    } catch (error) {
        console.error("Erreur lors de la récupération des données : ", error);
    }
}


// Fonction pour tracer les données
function plotData(xData, yData) {

    const xMin = Math.min(...xData);
    const xMax = Math.max(...xData);
    const yMin = Math.min(...yData);
    const yMax = Math.max(...yData);

    const xRange = xMax - xMin;
    const yRange = yMax - yMin;

    const startX = canvas.width* (xData[0]-xMin)/xRange;
    const startY = canvas.height*(yData[0]-yMin)/yRange;

    context.beginPath();
    context.moveTo(startX, startY);

    for (let i = 1; i < xData.length; i++) {
        context.lineTo(
            canvas.width* (xData[i]-xMin)/xRange,
            canvas.height*(yData[i]-yMin)/yRange);        
        }

    context.stroke();
    
}

function sortTwoLists(L, M) {
    if (L.length !== M.length) {
        throw new Error("Les deux listes doivent avoir la même longueur.");
    }

    // Associer les deux listes
    let combined = L.map((value, index) => ({ value, pairedValue: M[index] }));

    // Trier par la valeur de L
    combined.sort((a, b) => a.value - b.value);

    // Extraire les listes triées
    const sortedL = combined.map(item => item.value);
    const sortedM = combined.map(item => item.pairedValue);

    return { sortedL, sortedM };
}

// Fonction pour mettre à jour et recharger le graphique
function updatePlot(newYName) {
    yName = newYName;
    context.clearRect(0, 0, canvas.width, canvas.height); // Nettoyer le canvas
    fetchDataAndPlot(launchId, xName, yName); // Relancer avec la nouvelle donnée Y
}


// Appeler la fonction avec l'identifiant de lancement
launchId = 1 ; // Remplacez par l'ID réel ou récupérez-le dynamiquement
xName = "timestamp";
yName = "altitude" ; 
fetchDataAndPlot(launchId, xName, yName);

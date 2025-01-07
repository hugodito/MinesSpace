let canvas = document.getElementById("canvas");
let context = canvas.getContext("2d");

var window_height = 300;
var window_width = 500;

canvas.width = window_width;
canvas.height = window_height;

canvas.style.background = "#ddf";


/**
 * Fonction pour récupérer les données depuis l'API
 * 
 * @param {*} launchId defintion de ton parametre
 * @param {*} dataToPlotAsX 
 */
async function fetchDataAndPlot(launchId, dataNameToPlotAsX, dataNameToPlotAsY) {
    try {
        //const response = await fetch(`/api/data/${launchId}`);
        const response = await fetch(`../resources/mockDb.json`);
        if (!response.ok) {
            throw new Error(`Erreur API: ${response.statusText}`);
        }
        const apiData = await response.json();

        // Supposons que les données sont sous forme d'un tableau d'objets { time: ..., altitude: ... }
        const rawX = apiData.map(point => point[dataNameToPlotAsX]);
        const rawY = apiData.map(point => point[dataNameToPlotAsY]);

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

// Appeler la fonction avec l'identifiant de lancement
const launchId = 1 ; // Remplacez par l'ID réel ou récupérez-le dynamiquement
xName = "timestamp";
yName = "altitude" ; 
fetchDataAndPlot(launchId, xName, yName);

const totalCostElement = document.getElementById('total-cost');
const tradeAmountElement = document.getElementById('trade-amount');
const totalCost = totalCostElement.value / tradeAmountElement.value;

const updateTotalCost = () => {
	const tradeAmount = tradeAmountElement.value;
	totalCostElement.value = tradeAmount * totalCost;
}
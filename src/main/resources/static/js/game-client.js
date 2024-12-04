console.log("asd")
// Estado do jogo
let gameState = {
    selectedFighter: null,
    selectedPower: null,
    selectedTarget: null,
    isMyTurn: false,
    stompClient: null
};

// Conectar ao WebSocket
function connectWebSocket() {
    const socket = new SockJS('/ws');
    gameState.stompClient = Stomp.over(socket);

    gameState.stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // Inscrever no tópico do jogo
        const gameId = document.getElementById('game-status').dataset.gameId;
        gameState.stompClient.subscribe('/topic/game.' + gameId, function(response) {
            handleGameUpdate(JSON.parse(response.body));
        });
    });
}

// Seleção de Fighter
function selectFighter(element) {
    // Remove seleção anterior
    document.querySelectorAll('.fighter-card').forEach(card => {
        card.classList.remove('border-blue-500', 'border-2');
        card.querySelector('.fighter-select-status').classList.add('hidden');
    });

    // Adiciona seleção ao fighter atual
    element.classList.add('border-blue-500', 'border-2');
    element.querySelector('.fighter-select-status').classList.remove('hidden');

    gameState.selectedFighter = {
        id: element.dataset.fighterId,
        points: parseInt(element.dataset.points)
    };

    // Habilita powers válidos
    updateAvailablePowers();
}

// Seleção de Power
function selectPower(element) {
    if (!gameState.selectedFighter || element.classList.contains('disabled')) {
        return;
    }

    // Remove seleção anterior
    document.querySelectorAll('.power-card').forEach(card => {
        card.classList.remove('border-yellow-500', 'border-2');
    });

    // Adiciona seleção ao power atual
    element.classList.add('border-yellow-500', 'border-2');

    gameState.selectedPower = {
        id: element.dataset.powerId,
        value: parseInt(element.dataset.powerValue)
    };

    // Habilita seleção de alvo
    enableTargetSelection();
}

// Seleção de Alvo
function selectTarget(element) {
    if (!gameState.selectedFighter || !gameState.selectedPower || !element.dataset.active) {
        return;
    }

    // Remove seleção anterior
    document.querySelectorAll('.opponent-fighter').forEach(card => {
        card.classList.remove('border-red-500', 'border-2');
    });

    // Adiciona seleção ao alvo atual
    element.classList.add('border-red-500', 'border-2');

    gameState.selectedTarget = {
        id: element.dataset.fighterId
    };

    // Habilita botão de ataque
    enableAttackButton();
}

// Atualiza powers disponíveis baseado no fighter selecionado
function updateAvailablePowers() {
    const fighterPoints = gameState.selectedFighter?.points || 0;

    document.querySelectorAll('.power-card').forEach(power => {
        const powerValue = parseInt(power.dataset.powerValue);
        if (powerValue > fighterPoints) {
            power.classList.add('disabled', 'opacity-50', 'cursor-not-allowed');
        } else {
            power.classList.remove('disabled', 'opacity-50', 'cursor-not-allowed');
        }
    });
}

// Habilita seleção de alvo
function enableTargetSelection() {
    document.querySelectorAll('.opponent-fighter').forEach(fighter => {
        if (fighter.dataset.active === 'true') {
            fighter.classList.add('cursor-pointer', 'hover:bg-red-100');
        }
    });
}

// Habilita botão de ataque
function enableAttackButton() {
    const attackButton = document.getElementById('attack-button');
    if (attackButton) {
        attackButton.disabled = false;
        attackButton.classList.remove('opacity-50');
    }
}

// Executa ataque
function executeAttack() {
    if (!gameState.selectedFighter || !gameState.selectedPower || !gameState.selectedTarget) {
        return;
    }

    const gameId = document.getElementById('game-status').dataset.gameId;
    const moveRequest = {
        gameId: gameId,
        attackingFighterId: gameState.selectedFighter.id,
        attackPowerId: gameState.selectedPower.id,
        targetFighterId: gameState.selectedTarget.id
    };

    // Envia movimento via WebSocket
    gameState.stompClient.send("/app/game.move", {}, JSON.stringify(moveRequest));

    // Reseta seleções
    resetSelections();
}

// Reseta seleções
function resetSelections() {
    gameState.selectedFighter = null;
    gameState.selectedPower = null;
    gameState.selectedTarget = null;

    document.querySelectorAll('.fighter-card, .power-card, .opponent-fighter').forEach(element => {
        element.classList.remove('border-blue-500', 'border-2', 'border-yellow-500', 'border-red-500');
    });

    document.querySelectorAll('.fighter-select-status').forEach(element => {
        element.classList.add('hidden');
    });
}

// Atualiza o estado do jogo
function handleGameUpdate(gameState) {
    // Atualiza status do jogo
    document.getElementById('game-status').textContent = gameState.game.status;

    // Atualiza turno atual
    const currentTurnElement = document.getElementById('current-turn');
    if (currentTurnElement) {
        currentTurnElement.textContent = gameState.game.currentTurn.username;
    }

    // Atualiza fighters
    updateFighters(gameState.game);

    // Exibe mensagem se houver
    if (gameState.message) {
        showGameMessage(gameState.message);
    }
}

// Atualiza estado dos fighters
function updateFighters(game) {
    // Atualiza fighters do jogador
    game.player1Fighters.forEach(fighter => {
        const fighterElement = document.querySelector(`[data-fighter-id="${fighter.id}"]`);
        if (fighterElement) {
            fighterElement.querySelector('.points').textContent = fighter.points;
            fighterElement.dataset.active = fighter.active;
            updateFighterStatus(fighterElement, fighter.active);
        }
    });

    // Atualiza fighters do oponente
    game.player2Fighters.forEach(fighter => {
        const fighterElement = document.querySelector(`[data-fighter-id="${fighter.id}"]`);
        if (fighterElement) {
            fighterElement.querySelector('.points').textContent = fighter.points;
            fighterElement.dataset.active = fighter.active;
            updateFighterStatus(fighterElement, fighter.active);
        }
    });
}

// Atualiza status visual do fighter
function updateFighterStatus(element, active) {
    if (active) {
        element.classList.remove('bg-gray-50');
        element.classList.add('bg-blue-50');
    } else {
        element.classList.remove('bg-blue-50');
        element.classList.add('bg-gray-50');
    }
}

// Exibe mensagem do jogo
function showGameMessage(message) {
    const messageElement = document.getElementById('game-message');
    if (messageElement) {
        messageElement.textContent = message;
        messageElement.classList.remove('hidden');
        setTimeout(() => {
            messageElement.classList.add('hidden');
        }, 3000);
    }
}

// Inicializa o jogo quando a página carregar
document.addEventListener('DOMContentLoaded', function() {
    connectWebSocket();
    gameState.isMyTurn = document.querySelector('[data-is-my-turn="true"]') !== null;
});
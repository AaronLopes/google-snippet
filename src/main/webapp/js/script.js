window.addEventListener('DOMContentLoaded', (event) => {
  const cards = document.getElementsByClassName('card');
  initVote(cards);
  for (const card of cards) {
    const up = card.getElementsByClassName('upvote')[0];
    const down = card.getElementsByClassName('downvote')[0];
    up.addEventListener('click', function() {
      renderVote(card, 'upvote');
    });
    down.addEventListener('click', function() {
      renderVote(card, 'downvote');
    });
  }
});

function initVote(cards) {
  for (const card of cards) {
    fetch('/vote?url=' + card.value).then((res) => res.json()).then((json) => {
      toggleButtons(card, json);
    });
  }
}

async function renderVote(card, action) {
  const json = await updateVote(card.value, action);
  toggleButtons(card, json);
}

function toggleButtons(card, json) {
  const total = card.getElementsByClassName('total-votes')[0];
  if (Object.keys(json).length !== 0) {
    total.innerHTML = json.totalvotes;
    const up = card.getElementsByClassName('upvote')[0];
    const down = card.getElementsByClassName('downvote')[0];
    if (json.toggleUpvote === 'active') {
      up.style.color = 'green';
    } else {
      up.style.color = 'black';
    }
    if (json.toggleDownvote === 'active') {
      down.style.color = 'red';
    } else {
      down.style.color = 'black';
    }
  }
}

async function updateVote(url, action) {
  const response = await fetch('/vote', {
    method: 'POST',
    body: 'url=' + url + '&' + action,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  });
  return response.json();
}

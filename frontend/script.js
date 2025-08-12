// Initialize Swiper
const swiper = new Swiper('.swiper', {
  slidesPerView: 1,
  loop: true,
  autoplay: {
    delay: 4000,
    disableOnInteraction: false,
  },
  pagination: {
    el: '.slideshow .swiper-pagination',
    clickable: true,
  },
  navigation: {
    nextEl: '.slideshow .swiper-button-next',
    prevEl: '.slideshow .swiper-button-prev',
  },
});

// --- NEW: Trending Section Scroller Logic ---
  const trendingRow = document.getElementById('trending-row-js');
  const nextButton = document.getElementById('trending-next-btn');

  // Check if these elements exist on the page to avoid errors
  if (trendingRow && nextButton) {
    nextButton.addEventListener('click', () => {
      // Calculate how much to scroll based on the visible width of the row
      const scrollAmount = trendingRow.clientWidth;

      // Check if we are near the end of the scrollable content
      const isAtEnd = trendingRow.scrollLeft + scrollAmount >= trendingRow.scrollWidth - trendingRow.clientWidth;

      if (isAtEnd) {
        // If at the end, scroll back to the beginning for a loop effect
        trendingRow.scrollLeft = 0;
      } else {
        // Otherwise, scroll to the next "page" of items
        trendingRow.scrollLeft += scrollAmount;
      }
    });
  }

;


const counters = document.querySelectorAll('.stat-number');
let started = false;

function isInViewport(elem) {
  const rect = elem.getBoundingClientRect();
  return rect.top < window.innerHeight && rect.bottom >= 0;
}

function animateCounters() {
  if (!started && isInViewport(counters[0])) {
    counters.forEach(counter => {
      const target = +counter.getAttribute('data-target');
      let count = 0;
      const step = Math.ceil(target / 100);

      const update = () => {
        if (count < target) {
          count += step;
          if (count > target) count = target;
          counter.innerText = count;
          setTimeout(update, 20);
        }
      };

      update();
    });
    started = true;
  }
}

window.addEventListener('scroll', animateCounters);

const scrollElements = document.querySelectorAll('.scroll-animate');

const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('show');
    }
  });
}, {
  threshold: 0.1
});

scrollElements.forEach(el => observer.observe(el));
// No need to re-initialize Swiper in DOMContentLoaded, already initialized above
const loginForm = document.querySelector('.login-form'); // Make sure your login form has this class

    if (loginForm) {
        loginForm.addEventListener('submit', async function (event) {
            // Prevent the default browser form submission
            event.preventDefault();

            // Get the username and password from the form fields
            const usernameInput = document.getElementById('username'); // Ensure your input has this ID
            const passwordInput = document.getElementById('password'); // Ensure your input has this ID
            
            const username = usernameInput.value;
            const password = passwordInput.value;

            // Prepare the data to send to the backend
            const loginData = {
                username: username,
                password: password
            };

            try {
                // Send the data to the /api/login endpoint
                const response = await fetch('/api/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(loginData)
                });

                if (response.ok) {
                    // If login is successful (status 200-299)
                    const data = await response.json();
                    
                    // Store the token in the browser's local storage
                    localStorage.setItem('jwtToken', data.token);

                    alert('Login successful!');
                    // Redirect to the user's profile page or homepage
                    window.location.href = '/userprofile.html'; // Or '/codenet.html'
                    
                } else {
                    // If login fails (e.g., status 401 Unauthorized)
                    alert('Login failed. Please check your username and password.');
                }
            } catch (error) {
                // Handle network errors, etc.
                console.error('An error occurred during login:', error);
                alert('An error occurred. Please try again later.');
            }
        });
    }

    document.querySelector('.submit-form').addEventListener('submit', async function(e) { 
    e.preventDefault(); 
 
    const authToken = await getAuthToken(); 
    if (!authToken) { 
        alert('Please log in to submit a project.'); 
        window.location.href = 'login.html'; 
        return; 
    } 
 
    const title = document.getElementById('title').value; 
    const subtitle = document.getElementById('subtitle').value; 
    const description = document.getElementById('description').value; 
    const mediaFiles = document.getElementById('media').files; 
 
    const formData = new FormData(); 
    // Append project details as a JSON string 
    const projectData = { title, subtitle, description }; 
    formData.append('project', new Blob([JSON.stringify(projectData)], { type: 'application/json' 
})); 
 
    // Append media files 
    for (let i = 0; i < mediaFiles.length; i++) { 
        formData.append('media', mediaFiles[i]); 
    } 
 
    try { 
        const response = await fetch('http://localhost:8080/api/projects', { // Replace with your backend URL 
            method: 'POST', 
            headers: { 
                'Authorization': `Bearer ${authToken}` 
            }, 
            body: formData // No 'Content-Type' header needed for FormData; browser sets it 
        }); 
 
        if (response.ok) { 
            alert('Project submitted successfully!'); 
            // Clear form or redirect 
            this.reset(); 
        } else { 
            const error = await response.json(); 
            alert(`Failed to submit project: ${error.message || response.statusText}`); 
        } 
    } catch (error) { 
        console.error('Error submitting project:', error); 
        alert('An error occurred while submitting the project.'); 
    } 
}); 

// Fetch and update trending projects
async function fetchTrendingProjects() {
  try {
    const res = await fetch('/api/trending');
    if (!res.ok) throw new Error('Failed to fetch trending projects');
    const projects = await res.json();

    const trendingRow = document.getElementById('trending-row-js');
    trendingRow.innerHTML = ''; // Clear existing

    projects.forEach(project => {
      const article = document.createElement('article');
      article.classList.add('trending-item');
      article.innerHTML = `
        <a href="${project.url || '#'}" class="trending-poster" title="${project.title}">
          <img src="${project.image}" alt="${project.title}">
        </a>
      `;
      trendingRow.appendChild(article);
    });
  } catch (error) {
    console.error(error);
  }
}

// Animate stat counters to count up smoothly
function animateCount(element, target) {
  let count = 0;
  const increment = target / 100; // Adjust for speed
  const interval = setInterval(() => {
    count += increment;
    if (count >= target) {
      count = target;
      clearInterval(interval);
    }
    element.textContent = Math.floor(count);
  }, 20);
}

// Fetch and update stats
async function fetchStats() {
  try {
    const res = await fetch('/api/stats');
    if (!res.ok) throw new Error('Failed to fetch stats');
    const stats = await res.json();

    // Map your stats to elements
    const mapping = {
      projectsUploaded: document.querySelector('.stat-number[data-target="50"]'),
      collaborators: document.querySelector('.stat-number[data-target="100"]'),
      soldProjects: document.querySelector('.stat-number[data-target="12"]'),
      users: document.querySelector('.stat-number[data-target="250"]'),
    };

    if (mapping.projectsUploaded) animateCount(mapping.projectsUploaded, stats.projectsUploaded);
    if (mapping.collaborators) animateCount(mapping.collaborators, stats.collaborators);
    if (mapping.soldProjects) animateCount(mapping.soldProjects, stats.soldProjects);
    if (mapping.users) animateCount(mapping.users, stats.users);
  } catch (error) {
    console.error(error);
  }
}

// On page load, fetch both
window.addEventListener('DOMContentLoaded', () => {
  fetchTrendingProjects();
  fetchStats();
});

// API Client for CODENET Backend
class CodenetAPI {
    constructor() {
        // Use environment configuration for API base URL
        this.baseURL = window.AppConfig ? window.AppConfig.getApiBaseUrl() : 'http://localhost:8080/api';
        this.authToken = null;
        this.requestCount = 0;
        this.rateLimitDelay = 1000; // Start with 1 second delay for rate limiting
    }

    // Authentication
    async getAuthToken() {
        if (window.Clerk && window.Clerk.session) {
            return await window.Clerk.session.getToken();
        }
        return null;
    }

    async setAuthToken() {
        this.authToken = await this.getAuthToken();
    }

    // Helper method for API calls with error handling and loading states
    async makeRequest(endpoint, options = {}, loadingElement = null, loadingMessage = 'Loading...') {
        await this.setAuthToken();
        
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        if (this.authToken) {
            config.headers['Authorization'] = `Bearer ${this.authToken}`;
        }

        try {
            // Show loading
            if (loadingElement) {
                this.showLoading(loadingElement, loadingMessage);
            }

            // Rate limiting: add delay for repeated requests
            if (this.requestCount > 0) {
                await new Promise(resolve => setTimeout(resolve, Math.min(this.rateLimitDelay, 5000)));
            }
            this.requestCount++;

            const response = await fetch(`${this.baseURL}${endpoint}`, config);
            
            // Handle rate limiting
            if (response.status === 429) {
                this.rateLimitDelay = Math.min(this.rateLimitDelay * 2, 30000); // Exponential backoff
                throw new Error('Rate limit exceeded. Please slow down your requests.');
            } else if (response.ok) {
                this.rateLimitDelay = 1000; // Reset delay on success
            }
            
            if (!response.ok) {
                let errorMessage = `HTTP ${response.status}: ${response.statusText}`;
                
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorData.error || errorMessage;
                } catch (e) {
                    // If response is not JSON, use default message
                }
                
                throw new Error(errorMessage);
            }
            
            const contentType = response.headers.get('content-type');
            let data;
            
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                data = await response.text();
            }
            
            return data;
            
        } catch (error) {
            // Log errors in development
            if (window.AppConfig && window.AppConfig.shouldLogDebug()) {
                console.error('API request failed:', error);
            }
            
            // Handle different error types
            let userMessage = 'An unexpected error occurred';
            
            if (error.message.includes('Failed to fetch')) {
                userMessage = 'Unable to connect to server. Please check your internet connection.';
            } else if (error.message.includes('401')) {
                userMessage = 'Please log in to continue.';
            } else if (error.message.includes('403')) {
                userMessage = 'You don\'t have permission to perform this action.';
            } else if (error.message.includes('404')) {
                userMessage = 'The requested resource was not found.';
            } else if (error.message.includes('429')) {
                userMessage = 'Too many requests. Please wait before trying again.';
            } else if (error.message.includes('500')) {
                userMessage = 'Server error. Please try again later.';
            } else {
                userMessage = error.message;
            }
            
            this.showError(userMessage, loadingElement);
            throw error;
        } finally {
            // Hide loading
            if (loadingElement) {
                this.hideLoading(loadingElement);
            }
            
            // Decrement request count after delay
            setTimeout(() => {
                this.requestCount = Math.max(0, this.requestCount - 1);
            }, 1000);
        }
    }

    // UI Helper Methods
    showLoading(element, message = 'Loading...') {
        if (element) {
            element.innerHTML = `
                <div class="loading-spinner">
                    <div class="spinner"></div>
                    <span>${message}</span>
                </div>
            `;
        }
    }

    hideLoading(element) {
        if (element && element.querySelector('.loading-spinner')) {
            element.querySelector('.loading-spinner').remove();
        }
    }

    showError(message, element = null) {
        console.error('API Error:', message);
        
        // Show toast notification
        this.showToast(message, 'error');
        
        // Show in specific element if provided
        if (element) {
            element.innerHTML = `
                <div class="error-message">
                    <i class="error-icon">⚠️</i>
                    <span>${message}</span>
                    <button onclick="location.reload()" class="retry-btn">Retry</button>
                </div>
            `;
        }
    }

    showSuccess(message) {
        console.log('Success:', message);
        this.showToast(message, 'success');
    }

    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <div class="toast-content">
                <span class="toast-icon">${type === 'error' ? '❌' : type === 'success' ? '✅' : 'ℹ️'}</span>
                <span class="toast-message">${message}</span>
                <button class="toast-close" onclick="this.parentElement.parentElement.remove()">×</button>
            </div>
        `;
        
        // Add to page
        if (!document.querySelector('.toast-container')) {
            const container = document.createElement('div');
            container.className = 'toast-container';
            document.body.appendChild(container);
        }
        
        document.querySelector('.toast-container').appendChild(toast);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (toast.parentElement) {
                toast.remove();
            }
        }, 5000);
    }

    // Project APIs
    async createProject(projectData, mediaFiles = []) {
        const formData = new FormData();
        
        // Add project data as JSON
        formData.append('project', new Blob([JSON.stringify(projectData)], { 
            type: 'application/json' 
        }));
        
        // Add media files
        mediaFiles.forEach(file => {
            formData.append('media', file);
        });

        await this.setAuthToken();
        
        const response = await fetch(`${this.baseURL}/projects`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${this.authToken}`
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error(`Failed to create project: ${response.statusText}`);
        }

        return await response.json();
    }

    async getProjects(search = null, category = null, technology = null) {
        let endpoint = '/projects';
        const params = new URLSearchParams();
        
        if (search) params.append('search', search);
        if (category) params.append('category', category);
        if (technology) params.append('technology', technology);
        
        if (params.toString()) {
            endpoint += `?${params.toString()}`;
        }
        
        return await this.makeRequest(endpoint);
    }

    async getProjectById(id) {
        return await this.makeRequest(`/projects/${id}`);
    }

    async getMyProjects() {
        return await this.makeRequest('/projects/my');
    }

    async updateProject(id, projectData) {
        return await this.makeRequest(`/projects/${id}`, {
            method: 'PUT',
            body: JSON.stringify(projectData)
        });
    }

    async deleteProject(id) {
        return await this.makeRequest(`/projects/${id}`, {
            method: 'DELETE'
        });
    }

    async likeProject(id) {
        return await this.makeRequest(`/projects/${id}/like`, {
            method: 'PUT'
        });
    }

    async getTrendingProjects() {
        return await this.makeRequest('/projects/trending');
    }

    async getFeaturedProjects() {
        return await this.makeRequest('/projects/featured');
    }

    async getMostViewedProjects() {
        return await this.makeRequest('/projects/most-viewed');
    }

    // User APIs
    async getCurrentUser() {
        return await this.makeRequest('/users/me');
    }

    async getUserById(userId) {
        return await this.makeRequest(`/users/${userId}`);
    }

    // Stats API
    async getStats() {
        return await this.makeRequest('/projects/stats');
    }

    // Check if user is authenticated
    isAuthenticated() {
        return this.authToken !== null;
    }

    // Redirect to login if not authenticated
    async requireAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = 'login.html';
            return false;
        }
        return true;
    }
}

// Global API instance
const api = new CodenetAPI();

// Utility functions for common operations
const CodenetUtils = {
    // Format date
    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString();
    },

    // Format number with commas
    formatNumber(num) {
        return num.toLocaleString();
    },

    // Show loading spinner
    showLoading(element) {
        element.innerHTML = '<div class="loading">Loading...</div>';
    },

    // Show error message
    showError(element, message) {
        element.innerHTML = `<div class="error">${message}</div>`;
    },

    // Create project card HTML
    createProjectCard(project) {
        return `
            <div class="project-card" data-id="${project.id}">
                <div class="project-image">
                    ${project.mediaUrls && project.mediaUrls.length > 0 
                        ? `<img src="${project.mediaUrls[0]}" alt="${project.title}">`
                        : '<div class="no-image">No Image</div>'
                    }
                </div>
                <div class="project-info">
                    <h3>${project.title}</h3>
                    <p class="subtitle">${project.subtitle || ''}</p>
                    <p class="description">${project.description}</p>
                    <div class="project-meta">
                        <span class="owner">By ${project.ownerName || 'Unknown'}</span>
                        <span class="likes">❤️ ${project.likes}</span>
                        <span class="views">👁️ ${project.views}</span>
                    </div>
                    <div class="project-actions">
                        <button onclick="likeProject('${project.id}')" class="btn-like">Like</button>
                        <button onclick="viewProject('${project.id}')" class="btn-view">View</button>
                    </div>
                </div>
            </div>
        `;
    },

    // Update stats display
    updateStats(stats) {
        const statsElements = document.querySelectorAll('[data-stat]');
        statsElements.forEach(element => {
            const statName = element.getAttribute('data-stat');
            if (stats[statName] !== undefined) {
                element.textContent = this.formatNumber(stats[statName]);
            }
        });
    }
};

// Global event handlers
async function likeProject(projectId) {
    try {
        await api.likeProject(projectId);
        // Update the like count in the UI
        const likeElement = document.querySelector(`[data-id="${projectId}"] .likes`);
        if (likeElement) {
            const currentLikes = parseInt(likeElement.textContent.match(/\d+/)[0]);
            likeElement.textContent = `❤️ ${currentLikes + 1}`;
        }
    } catch (error) {
        console.error('Failed to like project:', error);
        alert('Failed to like project. Please try again.');
    }
}

async function viewProject(projectId) {
    try {
        const project = await api.getProjectById(projectId);
        // You can implement a modal or redirect to a project detail page
        console.log('Viewing project:', project);
        // For now, just log the project details
    } catch (error) {
        console.error('Failed to view project:', error);
        alert('Failed to load project details.');
    }
}

// Initialize API when page loads
document.addEventListener('DOMContentLoaded', async () => {
    try {
        await api.setAuthToken();
        
        // Check if user is authenticated for protected pages
        const protectedPages = ['submitproject.html', 'userprofile.html'];
        const currentPage = window.location.pathname.split('/').pop();
        
        if (protectedPages.includes(currentPage)) {
            if (!await api.requireAuth()) {
                return; // Redirected to login
            }
        }
        
        // Load page-specific content
        await loadPageContent();
        
    } catch (error) {
        console.error('Failed to initialize API:', error);
    }
});

// Load content based on current page
async function loadPageContent() {
    const currentPage = window.location.pathname.split('/').pop();
    
    try {
        switch (currentPage) {
            case 'codenet.html':
                await loadHomePage();
                break;
            case 'codelibrary.html':
                await loadCodeLibrary();
                break;
            case 'userprofile.html':
                await loadUserProfile();
                break;
            case 'submitproject.html':
                await loadSubmitProject();
                break;
        }
    } catch (error) {
        console.error('Failed to load page content:', error);
    }
}

// Page-specific loading functions
async function loadHomePage() {
    try {
        const [trendingProjects, stats] = await Promise.all([
            api.getTrendingProjects(),
            api.getStats()
        ]);
        
        // Update trending projects
        const trendingContainer = document.querySelector('.trending-projects');
        if (trendingContainer) {
            trendingContainer.innerHTML = trendingProjects
                .map(project => CodenetUtils.createProjectCard(project))
                .join('');
        }
        
        // Update stats
        CodenetUtils.updateStats(stats);
        
    } catch (error) {
        console.error('Failed to load home page:', error);
    }
}

async function loadCodeLibrary() {
    try {
        const projects = await api.getProjects();
        const projectsContainer = document.querySelector('.project-list');
        
        if (projectsContainer) {
            projectsContainer.innerHTML = projects
                .map(project => CodenetUtils.createProjectCard(project))
                .join('');
        }
        
    } catch (error) {
        console.error('Failed to load code library:', error);
    }
}

async function loadUserProfile() {
    try {
        const [currentUser, myProjects] = await Promise.all([
            api.getCurrentUser(),
            api.getMyProjects()
        ]);
        
        // Update user info
        const userInfoContainer = document.querySelector('.user-info');
        if (userInfoContainer) {
            userInfoContainer.innerHTML = `
                <h2>${currentUser.firstName} ${currentUser.lastName}</h2>
                <p>${currentUser.email}</p>
                <p>Username: ${currentUser.username}</p>
            `;
        }
        
        // Update user's projects
        const myProjectsContainer = document.querySelector('.my-projects');
        if (myProjectsContainer) {
            myProjectsContainer.innerHTML = myProjects
                .map(project => CodenetUtils.createProjectCard(project))
                .join('');
        }
        
    } catch (error) {
        console.error('Failed to load user profile:', error);
    }
}

async function loadSubmitProject() {
    // Set up form submission
    const form = document.querySelector('.submit-form');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            try {
                const formData = new FormData(form);
                const projectData = {
                    title: formData.get('title'),
                    subtitle: formData.get('subtitle'),
                    description: formData.get('description'),
                    category: formData.get('category'),
                    technology: formData.get('technology'),
                    githubUrl: formData.get('githubUrl'),
                    liveDemoUrl: formData.get('liveDemoUrl'),
                    tags: formData.get('tags').split(',').map(tag => tag.trim())
                };
                
                const mediaFiles = formData.getAll('media');
                
                await api.createProject(projectData, mediaFiles);
                alert('Project submitted successfully!');
                form.reset();
                
            } catch (error) {
                console.error('Failed to submit project:', error);
                alert('Failed to submit project. Please try again.');
            }
        });
    }
} 
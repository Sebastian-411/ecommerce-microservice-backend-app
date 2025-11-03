"""
Locust performance and stress tests for E-commerce Microservices
This file defines load testing scenarios for the e-commerce application
"""

from locust import HttpUser, task, between, SequentialTaskSet
import random
import json

class ECommerceUser(HttpUser):
    """
    Base user class that simulates typical e-commerce user behavior
    """
    wait_time = between(1, 3)  # Wait between 1 and 3 seconds between tasks
    
    def on_start(self):
        """
        Called when a simulated user starts - simulates user login/registration
        """
        self.user_id = None
        self.product_ids = []
        self.order_id = None
        
        # Register or login user
        user_data = {
            "firstName": f"LoadTestUser{random.randint(1000, 9999)}",
            "lastName": "Test",
            "email": f"loadtest{random.randint(1000, 9999)}@example.com",
            "phone": f"{random.randint(1000000000, 9999999999)}"
        }
        
        with self.client.post("/api/users", json=user_data, catch_response=True) as response:
            if response.status_code == 200:
                try:
                    user_response = response.json()
                    self.user_id = user_response.get("userId")
                    response.success()
                except:
                    response.failure("Failed to parse user response")
            else:
                response.failure(f"User creation failed with status {response.status_code}")
    
    @task(3)
    def browse_products(self):
        """
        Task: Browse product catalog (high frequency)
        Weight: 3 (executed 3 times more often than other tasks)
        """
        with self.client.get("/api/products", catch_response=True) as response:
            if response.status_code == 200:
                try:
                    products = response.json()
                    if "collection" in products:
                        product_list = products["collection"]
                        if product_list:
                            # Store product IDs for later use
                            self.product_ids = [p.get("productId") for p in product_list if p.get("productId")]
                    response.success()
                except:
                    response.failure("Failed to parse products response")
            else:
                response.failure(f"Failed to get products with status {response.status_code}")
    
    @task(2)
    def view_product_details(self):
        """
        Task: View specific product details
        Weight: 2
        """
        if not self.product_ids:
            # If no products cached, get products first
            with self.client.get("/api/products", catch_response=True) as response:
                if response.status_code == 200:
                    try:
                        products = response.json()
                        if "collection" in products:
                            product_list = products["collection"]
                            if product_list:
                                self.product_ids = [p.get("productId") for p in product_list if p.get("productId")]
                    except:
                        pass
        
        if self.product_ids:
            product_id = random.choice(self.product_ids)
            with self.client.get(f"/api/products/{product_id}", catch_response=True) as response:
                if response.status_code == 200:
                    response.success()
                elif response.status_code == 404:
                    response.success()  # Product might not exist, still consider success
                else:
                    response.failure(f"Failed to get product with status {response.status_code}")
    
    @task(1)
    def create_product(self):
        """
        Task: Create a new product (admin action simulation)
        Weight: 1
        """
        product_data = {
            "productName": f"LoadTest Product {random.randint(1000, 9999)}",
            "productDescription": f"Product created during load testing - {random.randint(1000, 9999)}",
            "productPrice": round(random.uniform(10.0, 1000.0), 2),
            "productStockQuantity": random.randint(1, 1000)
        }
        
        with self.client.post("/api/products", json=product_data, catch_response=True) as response:
            if response.status_code == 200:
                try:
                    product_response = response.json()
                    product_id = product_response.get("productId")
                    if product_id:
                        self.product_ids.append(product_id)
                    response.success()
                except:
                    response.failure("Failed to parse product creation response")
            else:
                response.failure(f"Product creation failed with status {response.status_code}")
    
    @task(1)
    def view_user_profile(self):
        """
        Task: View user profile
        Weight: 1
        """
        if self.user_id:
            with self.client.get(f"/api/users/{self.user_id}", catch_response=True) as response:
                if response.status_code == 200:
                    response.success()
                else:
                    response.failure(f"Failed to get user with status {response.status_code}")
    
    @task(1)
    def create_order(self):
        """
        Task: Create an order
        Weight: 1
        """
        if not self.product_ids:
            return
        
        # Get a random product for the order
        product_id = random.choice(self.product_ids)
        
        # Get product details to calculate price
        with self.client.get(f"/api/products/{product_id}", catch_response=True) as response:
            if response.status_code == 200:
                try:
                    product = response.json()
                    product_price = product.get("productPrice", 0)
                    
                    # Create order
                    order_data = {
                        "orderStatus": "PENDING",
                        "orderTotal": float(product_price)
                    }
                    
                    with self.client.post("/api/orders", json=order_data, catch_response=True) as response:
                        if response.status_code == 200:
                            try:
                                order_response = response.json()
                                self.order_id = order_response.get("orderId")
                                response.success()
                            except:
                                response.failure("Failed to parse order creation response")
                        else:
                            response.failure(f"Order creation failed with status {response.status_code}")
                except:
                    response.failure("Failed to parse product response")


class SequentialOrderFlow(SequentialTaskSet):
    """
    Sequential task set that simulates a complete order flow
    """
    
    def on_start(self):
        """
        Initialize order flow
        """
        self.product_id = None
        self.order_id = None
    
    @task
    def step1_browse_products(self):
        """
        Step 1: Browse products
        """
        with self.client.get("/api/products", catch_response=True) as response:
            if response.status_code == 200:
                try:
                    products = response.json()
                    if "collection" in products:
                        product_list = products["collection"]
                        if product_list:
                            random_product = random.choice(product_list)
                            self.product_id = random_product.get("productId")
                except:
                    pass
                response.success()
            else:
                response.failure(f"Failed to browse products")
    
    @task
    def step2_view_product(self):
        """
        Step 2: View product details
        """
        if self.product_id:
            with self.client.get(f"/api/products/{self.product_id}", catch_response=True) as response:
                if response.status_code == 200:
                    response.success()
                else:
                    response.failure(f"Failed to view product")
    
    @task
    def step3_create_order(self):
        """
        Step 3: Create order
        """
        if self.product_id:
            # Get product price
            with self.client.get(f"/api/products/{self.product_id}", catch_response=True) as response:
                if response.status_code == 200:
                    try:
                        product = response.json()
                        price = product.get("productPrice", 0)
                        
                        order_data = {
                            "orderStatus": "PENDING",
                            "orderTotal": float(price)
                        }
                        
                        with self.client.post("/api/orders", json=order_data, catch_response=True) as response:
                            if response.status_code == 200:
                                try:
                                    order_response = response.json()
                                    self.order_id = order_response.get("orderId")
                                    response.success()
                                except:
                                    response.failure("Failed to parse order")
                            else:
                                response.failure("Failed to create order")
                    except:
                        response.failure("Failed to get product price")


class OrderFlowUser(HttpUser):
    """
    User class that performs sequential order flow
    """
    wait_time = between(2, 5)
    tasks = [SequentialOrderFlow]


class HighLoadUser(HttpUser):
    """
    User class for high-load stress testing
    Performs rapid API calls to test system under stress
    """
    wait_time = between(0.5, 1.5)  # Faster wait time for stress testing
    
    @task(10)
    def rapid_api_calls(self):
        """
        Rapid API calls to stress test the system
        """
        endpoints = [
            "/api/products",
            "/api/users",
            "/api/orders"
        ]
        
        endpoint = random.choice(endpoints)
        with self.client.get(endpoint, catch_response=True) as response:
            if response.status_code in [200, 404]:
                response.success()
            else:
                response.failure(f"Unexpected status {response.status_code}")
    
    @task(5)
    def concurrent_operations(self):
        """
        Simulate concurrent operations
        """
        # Get products
        with self.client.get("/api/products", catch_response=True) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Failed with status {response.status_code}")


class StressTestUser(HttpUser):
    """
    User class for extreme stress testing
    """
    wait_time = between(0.1, 0.5)  # Very fast for maximum load
    
    @task
    def stress_api(self):
        """
        Stress test API endpoints
        """
        # Mix of GET and POST requests
        operations = [
            lambda: self.client.get("/api/products"),
            lambda: self.client.get("/api/users"),
            lambda: self.client.get("/api/orders"),
        ]
        
        operation = random.choice(operations)
        with operation() as response:
            if response.status_code in [200, 201, 404]:
                response.success()
            else:
                response.failure(f"Status {response.status_code}")


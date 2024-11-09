<template>
  <div class="wide">
    <NavBar />

    <div class="card flex wrapper">
      <div class="products main">
        <div v-for="product in products" :key="product.id" class="product-card">
          <img :src="product.image" :alt="product.name" class="product-image" />
          <p class="product-price">{{ product.price }}</p>
          <h3 class="product-name">{{ product.name }}</h3>
          <a href="#" class="add-to-cart">Add to Cart</a>
          <p class="product-rating">{{ product.rating }}</p>
        </div>
      </div>

      <div class="sidebar">
        <h2>Please search filer here please</h2>
        <p>
          For Admin, this sidebar will become the edit/add-new form if needed
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import Env from "@/utils/Env";

export default {
  name: "ProductListPage",
  components: {
    NavBar,
  },
  data() {
    return {
      adminEmail: Env.ADMIN_EMAIL,
      // Products JSON data
      products: [
        {
          id: 1,
          name: "zPhone 10",
          price: "$1,300",
          image: require("@/assets/img/image.png"),
          rating: "4.2★",
          addToCartText: "Add to Cart",
        },
        {
          id: 2,
          name: "zNote W8",
          price: "$2,700",
          image: require("@/assets/img/image.png"),
          rating: "4.5★",
          addToCartText: "Add to Cart",
        },
        {
          id: 3,
          name: "zBook M5",
          price: "$4,700",
          image: require("@/assets/img/image.png"),
          rating: "4.8★",
          addToCartText: "Add to Cart",
        },
        {
          id: 4,
          name: "zBook M5",
          price: "$4,700",
          image: require("@/assets/img/image.png"),
          rating: "4.8★",
          addToCartText: "Add to Cart",
        },
        {
          id: 5,
          name: "zHeadphone OV9",
          price: "$1,300",
          image: require("@/assets/img/image.png"),
          rating: "4.4★",
          addToCartText: "Add to Cart",
        },
        {
          id: 6,
          name: "zNote W8",
          price: "$2,700",
          image: require("@/assets/img/image.png"),
          rating: "4.5★",
          addToCartText: "Add to Cart",
        },
      ],
    };
  },
  mounted() {
    console.log(process.env);
  },
};
</script>

<style scoped lang="scss">
.product-list {
  text-align: center;
  padding: 20px;
}

.products {
  display: flex;
  flex-wrap: wrap; /* Allows items to wrap to the next line */
  justify-content: center; /* Centers items horizontally */
  gap: 20px; /* Space between cards */
}

.product-card {
  text-align: center;
  width: 200px;
  margin: 10px; /* Optional: Space around each card */
}

.product-card:hover {
  transform: scale(1.05);
}

.product-image {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  margin-top: 5px;
  margin-bottom: 10px;
}

.product-price {
  font-size: 18px;
  color: #555;
  margin-top: 20px;
  margin-bottom: 0px;
}

.add-to-cart {
  color: #d9534f;
  text-decoration: none;
  font-weight: bold;
  display: inline-block;
  margin-top: 10px;
}

.product-rating {
  font-size: 14px;
  color: #888;
  margin-top: 5px;
}
</style>

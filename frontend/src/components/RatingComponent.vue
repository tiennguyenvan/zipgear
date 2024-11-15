<template>
  <div class="rating-component">
    <h3>Customer Ratings</h3>
    <div v-if="ratings.length > 0">
      <div v-for="rating in ratings" :key="rating.ratingId" class="rating-item">
        <p><strong>{{ rating.user.username }}:</strong> {{ rating.ratingDescription }}</p>
        <p>{{ rating.ratingStars }}★</p>
      </div>
    </div>
    <div v-else>
      <p>No ratings yet. Be the first to rate this product!</p>
    </div>

    <div class="add-rating" v-if="isLoggedIn">
      <h4>Add Your Rating</h4>
      <select v-model="newRatingStars" class="rating-stars">
        <option v-for="n in 5" :key="n" :value="n">{{ n }}★</option>
      </select>
      <textarea v-model="newRatingDescription" placeholder="Write your review..."></textarea>
      <button @click="submitRating">Submit Rating</button>
    </div>
    <div v-else>
      <p>Please log in to add a rating.</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "RatingComponent",
  props: {
    productId: {
      type: Number,
      required: true
    },
    isLoggedIn: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      ratings: [],
      newRatingStars: 5,
      newRatingDescription: ""
    };
  },
  created() {
    this.fetchRatings();
  },
  methods: {
    async fetchRatings() {
      try {
        const response = await fetch(`/api/ratings/${this.productId}`);
        if (response.ok) {
          this.ratings = await response.json();
        } else {
          console.error("Failed to fetch ratings.");
        }
      } catch (error) {
        console.error("Error fetching ratings:", error);
      }
    },
    async submitRating() {
      try {
        const requestBody = {
          ratingStars: this.newRatingStars,
          ratingDescription: this.newRatingDescription
        };
        const response = await fetch(`/api/ratings/${this.productId}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(requestBody)
        });
        if (response.ok) {
          alert("Rating submitted successfully!");
          this.newRatingStars = 5;
          this.newRatingDescription = "";
          this.fetchRatings();
        } else {
          console.error("Failed to submit rating.");
        }
      } catch (error) {
        console.error("Error submitting rating:", error);
      }
    }
  }
};
</script>

<style scoped>
.rating-component {
  margin-top: 2em;
}
.rating-item {
  margin-bottom: 1em;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.5em;
}
.add-rating {
  margin-top: 1em;
}
.rating-stars {
  margin-bottom: 0.5em;
}
textarea {
  width: 100%;
  height: 80px;
  margin-bottom: 0.5em;
}
button {
  background-color: #0073e6;
  color: white;
  border: none;
  padding: 0.5em 1em;
  cursor: pointer;
}
</style>

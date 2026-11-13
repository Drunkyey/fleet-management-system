<template>
  <v-container>
    <v-card class="pa-5 mt-5">
      <v-row align="center">
        <v-col cols="12" md="4">
          <v-card-title class="text-h4 px-0">Vehicule</v-card-title>
        </v-col>
        
        <!-- Filtrare -->
        <v-col cols="12" md="4">
          <v-text-field
            v-model="searchQuery"
            prepend-inner-icon="mdi-magnify"
            label="Caută vehicul (ex: BMW, CJ01ABC)"
            variant="outlined"
            density="compact"
            hide-details
          ></v-text-field>
        </v-col>

        <v-col cols="12" md="4" class="text-right">
          <v-btn color="success" @click="openCreateDialog">Adaugă Vehicul</v-btn>
        </v-col>
      </v-row>
      
      <v-table class="mt-4">
        <thead>
          <tr>
            <th class="text-left">ID</th>
            <th class="text-left">Număr</th>
            <th class="text-left">Brand</th>
            <th class="text-left">Model</th>
            <th class="text-left">An</th>
            <th class="text-left">Flota (ID)</th>
            <th class="text-right">Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="v in filteredVehicles" :key="v.id">
            <td>{{ v.id }}</td>
            <td><strong>{{ v.licensePlate }}</strong></td>
            <td>{{ v.brand }}</td>
            <td>{{ v.model }}</td>
            <td>{{ v.manufactureYear }}</td>
            <td>{{ v.fleet ? v.fleet.name : 'Fără flotă' }}</td>
            <td class="text-right">
              <v-btn icon size="small" color="blue" class="me-2" @click="openEditDialog(v)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon size="small" color="red" @click="deleteVehicle(v.id)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </td>
          </tr>
          <tr v-if="filteredVehicles.length === 0">
            <td colspan="7" class="text-center text-grey">Nu s-a găsit niciun vehicul.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Fereastra de Dialog pentru Adăugare / Editare Vehicul -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditMode ? 'Editează Vehiculul' : 'Adaugă Vehicul Nou' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentVehicle.brand" label="Brand (ex: BMW)" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentVehicle.model" label="Model (ex: 330i)" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentVehicle.licensePlate" label="Nr. Înmatriculare" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentVehicle.manufactureYear" label="An Fabricație" type="number" required></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-select
                  v-model="currentVehicle.fleet"
                  :items="fleets"
                  item-title="name"
                  item-value="id"
                  label="Selectează Flota"
                  return-object
                  required
                ></v-select>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">Anulează</v-btn>
          <v-btn color="success" variant="text" @click="saveVehicle">Salvează</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'VehicleManager',
  data() {
    return {
      vehicles: [],
      fleets: [],
      searchQuery: '',
      dialog: false,
      isEditMode: false,
      currentVehicle: {
        id: null,
        brand: '',
        model: '',
        licensePlate: '',
        manufactureYear: null,
        fleet: null
      }
    }
  },
  computed: {
    filteredVehicles() {
      if (!this.searchQuery) return this.vehicles;
      const lowerCaseQuery = this.searchQuery.toLowerCase();
      
      return this.vehicles.filter(v => 
        (v.brand && v.brand.toLowerCase().includes(lowerCaseQuery)) ||
        (v.licensePlate && v.licensePlate.toLowerCase().includes(lowerCaseQuery)) ||
        (v.model && v.model.toLowerCase().includes(lowerCaseQuery))
      );
    }
  },
  mounted() {
    this.fetchVehicles();
    this.fetchFleets();
  },
  methods: {
    fetchVehicles() {
      axios.get('http://localhost:8083/api/vehicles')
        .then(response => this.vehicles = response.data)
        .catch(err => console.error(err));
    },
    fetchFleets() {
      axios.get('http://localhost:8083/api/fleets')
        .then(response => this.fleets = response.data)
        .catch(err => console.error(err));
    },
    openCreateDialog() {
      this.isEditMode = false;
      this.currentVehicle = { id: null, brand: '', model: '', licensePlate: '', manufactureYear: null, fleet: null };
      this.dialog = true;
    },
    openEditDialog(vehicle) {
      this.isEditMode = true;
      this.currentVehicle = { ...vehicle }; // Copiem datele vehiculului selectat în formular
      this.dialog = true;
    },
    saveVehicle() {
      if (this.isEditMode) {
        // Apel PUT pentru actualizare
        axios.put(`http://localhost:8083/api/vehicles/${this.currentVehicle.id}`, this.currentVehicle)
          .then(() => {
            this.dialog = false;
            this.fetchVehicles();
          })
          .catch(error => console.error("Eroare la editare vehicul: ", error));
      } else {
        // Apel POST pentru creare
        axios.post('http://localhost:8083/api/vehicles', this.currentVehicle)
          .then(() => {
            this.dialog = false;
            this.fetchVehicles();
          })
          .catch(error => console.error("Eroare la adăugare vehicul: ", error));
      }
    },
    deleteVehicle(id) {
      if (confirm('Sigur doriți să ștergeți acest vehicul?')) {
        axios.delete(`http://localhost:8083/api/vehicles/${id}`)
          .then(() => {
            this.fetchVehicles();
          })
          .catch(err => console.error("Eroare la ștergere vehicul:", err));
      }
    }
  }
}
</script>
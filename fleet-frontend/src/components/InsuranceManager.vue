<template>
  <v-container>
    <v-card class="pa-5 mt-5">
      <v-row align="center">
        <v-col cols="12" md="8">
          <v-card-title class="text-h4 px-0">Polițe de Asigurare</v-card-title>
        </v-col>
        <v-col cols="12" md="4" class="text-right">
          <v-btn color="success" @click="openCreateDialog">Adaugă Poliță</v-btn>
        </v-col>
      </v-row>
      
      <v-table class="mt-4">
        <thead>
          <tr>
            <th class="text-left">ID</th>
            <th class="text-left">Vehicul (Nr. Înmatriculare)</th>
            <th class="text-left">Furnizor (Asigurător)</th>
            <th class="text-left">Număr Poliță</th>
            <th class="text-left">Valabilitate</th>
            <th class="text-left">Cost (RON)</th>
            <th class="text-right">Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="policy in policies" :key="policy.id">
            <td>{{ policy.id }}</td>
            <td><strong>{{ policy.vehicle ? policy.vehicle.licensePlate : 'N/A' }}</strong></td>
            <td>
              <v-chip size="small" variant="flat" color="indigo-lighten-4" style="color: black !important; font-weight: bold;">
                {{ policy.provider }}
              </v-chip>
            </td>
            <td>{{ policy.policyNumber }}</td>
            <td>{{ policy.startDate }} / {{ policy.endDate }}</td>
            <td>{{ policy.cost }}</td>
            <td class="text-right">
              <v-btn icon size="small" color="blue" class="me-2" @click="openEditDialog(policy)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon size="small" color="red" @click="deletePolicy(policy.id)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </td>
          </tr>
          <tr v-if="policies.length === 0">
            <td colspan="7" class="text-center text-grey">Nu există polițe de asigurare înregistrate.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Dialog Adăugare / Editare Poliță -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditMode ? 'Editează Polița' : 'Adaugă Poliță Nouă' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-select
                  v-model="currentPolicy.vehicle"
                  :items="vehicles"
                  item-title="licensePlate"
                  item-value="id"
                  label="Selectează Vehiculul"
                  return-object
                  required
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentPolicy.provider" label="Asigurător (ex: Allianz, Groupama)" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentPolicy.policyNumber" label="Număr Poliță" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field v-model="currentPolicy.startDate" label="Dată Început (YYYY-MM-DD)" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field v-model="currentPolicy.endDate" label="Dată Expirare (YYYY-MM-DD)" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field v-model.number="currentPolicy.cost" label="Cost (RON)" type="number" required></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">Anulează</v-btn>
          <v-btn color="success" variant="text" @click="savePolicy">Salvează</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'InsuranceManager',
  data() {
    return {
      policies: [],
      vehicles: [],
      dialog: false,
      isEditMode: false,
      currentPolicy: {
        id: null,
        vehicle: null,
        provider: '',
        policyNumber: '',
        startDate: '',
        endDate: '',
        cost: null
      }
    }
  },
  mounted() {
    this.fetchPolicies();
    this.fetchVehicles();
  },
  methods: {
    fetchPolicies() {
      axios.get('http://localhost:8083/api/insurance')
        .then(response => { this.policies = response.data; })
        .catch(error => { console.error('Eroare la preluarea polițelor:', error); });
    },
    fetchVehicles() {
      axios.get('http://localhost:8083/api/vehicles')
        .then(response => { this.vehicles = response.data; })
        .catch(error => { console.error('Eroare la preluarea vehiculelor:', error); });
    },
    openCreateDialog() {
      this.isEditMode = false;
      const today = new Date().toISOString().slice(0, 10);
      const nextYear = new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().slice(0, 10);
      this.currentPolicy = { 
        id: null,
        vehicle: null, 
        provider: '', 
        policyNumber: '', 
        startDate: today, 
        endDate: nextYear, 
        cost: null 
      };
      this.dialog = true;
    },
    openEditDialog(policy) {
      this.isEditMode = true;
      this.currentPolicy = { ...policy };
      this.dialog = true;
    },
    savePolicy() {
      if (this.isEditMode) {
        axios.put(`http://localhost:8083/api/insurance/${this.currentPolicy.id}`, this.currentPolicy)
          .then(() => {
            this.dialog = false;
            this.fetchPolicies();
          })
          .catch(error => console.error('Eroare la editare poliță:', error));
      } else {
        axios.post('http://localhost:8083/api/insurance', this.currentPolicy)
          .then(() => {
            this.dialog = false;
            this.fetchPolicies();
          })
          .catch(error => console.error('Eroare la salvare poliță:', error));
      }
    },
    deletePolicy(id) {
      if (confirm('Sigur doriți să ștergeți această poliță?')) {
        axios.delete(`http://localhost:8083/api/insurance/${id}`)
          .then(() => { this.fetchPolicies(); })
          .catch(err => console.error('Eroare la ștergere poliță:', err));
      }
    }
  }
}
</script>
import React, { useState, useEffect } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  FlatList,
  SafeAreaView,
  Alert,
  Linking,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import * as Notifications from 'expo-notifications';

const API_URL = 'http://192.168.0.133:8083';

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

export default function App() {
  const [vehicles, setVehicles] = useState([]);
  const [fleets, setFleets] = useState([]);
  const [activeAssignments, setActiveAssignments] = useState([]);
  const [expiringInsurances, setExpiringInsurances] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(false);

  // State pentru adăugare vehicul (identic cu VehicleManager.vue)
  const [newPlate, setNewPlate] = useState('');
  const [newBrand, setNewBrand] = useState('');
  const [newModel, setNewModel] = useState('');
  const [newYear, setNewYear] = useState('2022');
  const [selectedFleet, setSelectedFleet] = useState(null);

  useEffect(() => {
    requestNotificationPermissions();
    fetchAllData();
  }, []);

  const requestNotificationPermissions = async () => {
    const { status } = await Notifications.requestPermissionsAsync();
    if (status !== 'granted') {
      Alert.alert('Permisiune necesară', 'Activează notificările pentru alertele de asigurări.');
    }
  };

  // 1. VIEW: Preluare date din Spring Boot (inclusiv flotele)
  const fetchAllData = async () => {
    setLoading(true);
    try {
      const [vehRes, fleetRes, assignRes, insRes] = await Promise.all([
        fetch(`${API_URL}/api/vehicles`),
        fetch(`${API_URL}/api/fleets`),
        fetch(`${API_URL}/api/assignments/active`),
        fetch(`${API_URL}/api/insurance/expiring-soon`),
      ]);

      if (vehRes.ok) setVehicles(await vehRes.json());
      if (fleetRes.ok) {
        const fleetsData = await fleetRes.json();
        setFleets(fleetsData);
        if (fleetsData.length > 0 && !selectedFleet) {
          setSelectedFleet(fleetsData[0]);
        }
      }
      if (assignRes.ok) setActiveAssignments(await assignRes.json());
      if (insRes.ok) setExpiringInsurances(await insRes.json());
    } catch (error) {
      Alert.alert('Eroare conexiune', 'Nu m-am putut conecta la serverul Spring Boot.');
    } finally {
      setLoading(false);
    }
  };

  // VIEW FILTERED: Filtrare dinamică
  const filteredVehicles = vehicles.filter((v) => {
    const plate = (v.licensePlate || '').toLowerCase();
    const brand = (v.brand || '').toLowerCase();
    const model = (v.model || '').toLowerCase();
    const q = searchQuery.toLowerCase();
    return plate.includes(q) || brand.includes(q) || model.includes(q);
  });

  // 2. SEND DATA: Trimitere vehicul nou cu toate câmpurile cerute de backend
  const handleAddVehicle = async () => {
    if (!newPlate.trim() || !newBrand.trim() || !newModel.trim() || !newYear.trim()) {
      Alert.alert('Câmpuri incomplete', 'Completează numărul, marca, modelul și anul.');
      return;
    }

    if (!selectedFleet) {
      Alert.alert('Flotă lipsă', 'Selectează o flotă (sau creează una mai întâi pe site).');
      return;
    }

    try {
      const response = await fetch(`${API_URL}/api/vehicles`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          licensePlate: newPlate.trim().toUpperCase(),
          brand: newBrand.trim(),
          model: newModel.trim(),
          manufactureYear: parseInt(newYear, 10),
          fleet: selectedFleet,
        }),
      });

      if (response.ok) {
        Alert.alert('Succes', 'Vehiculul a fost salvat în baza de date!');
        setNewPlate('');
        setNewBrand('');
        setNewModel('');
        fetchAllData();
      } else {
        const errText = await response.text();
        Alert.alert('Eroare server', errText || 'Nu s-a putut salva vehiculul.');
      }
    } catch (error) {
      Alert.alert('Eroare', 'Nu s-au putut trimite datele către server.');
    }
  };

  // 3. NOTIFICATIONS: Notificare locală pe iPhone pentru asigurări scadente
  const triggerInsuranceNotification = async () => {
    const count = expiringInsurances.length;
    const message =
      count > 0
        ? `Atenție! Ai ${count} poliță/polițe de asigurare care expiră în mai puțin de 30 de zile.`
        : 'Toate polițele de asigurare ale flotei sunt valabile!';

    await Notifications.scheduleNotificationAsync({
      content: {
        title: 'Fleet Manager — Alertă Asigurări',
        body: message,
        sound: true,
      },
      trigger: null,
    });
  };

  // 4. SEND EMAIL: Deschide Mail cu raportul flotei
  const handleSendEmailReport = () => {
    const subject = encodeURIComponent('Raport Flotă Auto — Status Zilnic');
    const body = encodeURIComponent(
      `Salut,\n\nIată statusul curent al flotei:\n` +
        `- Total vehicule înregistrate: ${vehicles.length}\n` +
        `- Vehicule aflate acum pe traseu: ${activeAssignments.length}\n` +
        `- Polițe de asigurare scadente (< 30 zile): ${expiringInsurances.length}\n\n` +
        `Trimis din aplicația mobilă Fleet Manager.`
    );
    Linking.openURL(`mailto:?subject=${subject}&body=${body}`);
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Text style={styles.header}>Fleet Manager Mobile</Text>
        <Text style={styles.subHeader}>
          Active pe traseu: {activeAssignments.length} | Total flotă: {vehicles.length}
        </Text>

        {/* Acțiuni rapide: Notificare & Email */}
        <View style={styles.actionRow}>
          <TouchableOpacity style={styles.btnWarning} onPress={triggerInsuranceNotification}>
            <Text style={styles.btnText}>🔔 Alerte Asigurări ({expiringInsurances.length})</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.btnSecondary} onPress={handleSendEmailReport}>
            <Text style={styles.btnText}>✉️ Raport Email</Text>
          </TouchableOpacity>
        </View>

        {/* Secțiunea SEND DATA */}
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Adaugă Vehicul Nou (Send Data)</Text>
          
          <View style={styles.rowInputs}>
            <TextInput
              style={[styles.input, { flex: 1, marginRight: 8 }]}
              placeholder="Brand (ex: BMW)"
              value={newBrand}
              onChangeText={setNewBrand}
            />
            <TextInput
              style={[styles.input, { flex: 1 }]}
              placeholder="Model (ex: 330i)"
              value={newModel}
              onChangeText={setNewModel}
            />
          </View>

          <View style={styles.rowInputs}>
            <TextInput
              style={[styles.input, { flex: 1.5, marginRight: 8 }]}
              placeholder="Nr. Înmatriculare (ex: CJ01ABC)"
              value={newPlate}
              onChangeText={setNewPlate}
              autoCapitalize="characters"
            />
            <TextInput
              style={[styles.input, { flex: 1 }]}
              placeholder="An (ex: 2022)"
              value={newYear}
              onChangeText={setNewYear}
              keyboardType="numeric"
            />
          </View>

          {/* Selector Flotă */}
          <Text style={styles.label}>Selectează Flota:</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.fleetScroll}>
            {fleets.map((f) => {
              const isSelected = selectedFleet && selectedFleet.id === f.id;
              return (
                <TouchableOpacity
                  key={f.id}
                  style={[styles.fleetChip, isSelected && styles.fleetChipSelected]}
                  onPress={() => setSelectedFleet(f)}
                >
                  <Text style={[styles.fleetChipText, isSelected && styles.fleetChipTextSelected]}>
                    {f.name}
                  </Text>
                </TouchableOpacity>
              );
            })}
          </ScrollView>

          <TouchableOpacity style={styles.btnPrimary} onPress={handleAddVehicle}>
            <Text style={styles.btnText}>+ Salvează în Server</Text>
          </TouchableOpacity>
        </View>

        {/* Secțiunea VIEW (FILTERED) */}
        <View style={styles.card}>
          <View style={styles.listHeader}>
            <Text style={styles.cardTitle}>Vehicule Flotă (View Filtered)</Text>
            <TouchableOpacity onPress={fetchAllData}>
              <Text style={styles.refreshLink}>↻ Refresh</Text>
            </TouchableOpacity>
          </View>

          <TextInput
            style={styles.input}
            placeholder="Filtrează după număr, brand sau model..."
            value={searchQuery}
            onChangeText={setSearchQuery}
          />

          {loading ? (
            <ActivityIndicator size="large" color="#1976D2" style={{ marginVertical: 20 }} />
          ) : (
            <FlatList
              data={filteredVehicles}
              scrollEnabled={false}
              keyExtractor={(item, index) => (item.id ? item.id.toString() : index.toString())}
              ListEmptyComponent={<Text style={styles.emptyText}>Niciun vehicul găsit.</Text>}
              renderItem={({ item }) => (
                <View style={styles.vehicleItem}>
                  <View>
                    <Text style={styles.plateText}>{item.licensePlate || 'Fără număr'}</Text>
                    <Text style={styles.fleetSubText}>
                      {item.fleet ? item.fleet.name : 'Fără flotă'} • {item.manufactureYear || ''}
                    </Text>
                  </View>
                  <Text style={styles.modelText}>
                    {item.brand || ''} {item.model || ''}
                  </Text>
                </View>
              )}
            />
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F4F6F8' },
  scrollContent: { padding: 16, paddingBottom: 40 },
  header: { fontSize: 24, fontWeight: 'bold', color: '#1976D2', marginTop: 10 },
  subHeader: { fontSize: 14, color: '#555', marginBottom: 16 },
  actionRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 16 },
  card: {
    backgroundColor: '#FFF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 6,
    elevation: 3,
  },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#333', marginBottom: 12 },
  label: { fontSize: 13, fontWeight: '600', color: '#555', marginBottom: 6 },
  fleetScroll: { marginBottom: 14 },
  fleetChip: {
    paddingVertical: 6,
    paddingHorizontal: 14,
    borderRadius: 20,
    backgroundColor: '#ECEFF1',
    marginRight: 8,
  },
  fleetChipSelected: { backgroundColor: '#1976D2' },
  fleetChipText: { fontSize: 13, color: '#333', fontWeight: '500' },
  fleetChipTextSelected: { color: '#FFF', fontWeight: 'bold' },
  listHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  refreshLink: { color: '#1976D2', fontWeight: '600', marginBottom: 12 },
  input: {
    borderWidth: 1,
    borderColor: '#DDD',
    borderRadius: 8,
    padding: 10,
    marginBottom: 10,
    backgroundColor: '#FAFAFA',
  },
  rowInputs: { flexDirection: 'row' },
  btnPrimary: {
    backgroundColor: '#1976D2',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  btnWarning: {
    flex: 1,
    backgroundColor: '#E65100',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
    marginRight: 8,
  },
  btnSecondary: {
    flex: 1,
    backgroundColor: '#2E7D32',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  btnText: { color: '#FFF', fontWeight: 'bold', fontSize: 13 },
  vehicleItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#EEE',
  },
  plateText: { fontWeight: 'bold', fontSize: 15, color: '#111' },
  fleetSubText: { fontSize: 12, color: '#888', marginTop: 2 },
  modelText: { fontSize: 14, fontWeight: '500', color: '#444' },
  emptyText: { textAlign: 'center', color: '#888', marginVertical: 16 },
});
# Script pour générer les graphes

import matplotlib
matplotlib.use('TkAgg')  # Pour éviter le bug avec le backend InterAgg de PyCharm

import pandas as pd
import matplotlib.pyplot as plt
from io import StringIO

# Lire le fichier brut
with open("sortie.csv", encoding="utf-8") as f:
    lines = f.readlines()

# Trouver les indices des sections
section_indices = [i for i, line in enumerate(lines) if line.startswith("Temps")]
section_ends = section_indices[1:] + [len(lines)]

# Extraire les trois tableaux
dataframes = []
for start, end in zip(section_indices, section_ends):
    section = lines[start:end]
    df = pd.read_csv(StringIO("".join(section)))
    dataframes.append(df)

# Assigner les noms pour plus de clarté
population_df, aieux_df, aieules_df = dataframes

# Tracer les trois graphes
plt.figure(figsize=(15, 10))

# Population
plt.subplot(3, 1, 1)
plt.plot(population_df['Temps'], population_df['Taille de la population'], color='blue')
plt.title("Taille de la population au cours du temps")
plt.xlabel("Temps")
plt.ylabel("Population")

# Aieux
plt.subplot(3, 1, 2)
plt.plot(aieux_df['Temps'], aieux_df['Aieux'], color='green')
plt.title("Nombre d'aieux au cours du temps")
plt.xlabel("Temps")
plt.ylabel("Aieux")

# Aieules
plt.subplot(3, 1, 3)
plt.plot(aieules_df['Temps'], aieules_df['Aieules'], color='purple')
plt.title("Nombre d'aieules au cours du temps")
plt.xlabel("Temps")
plt.ylabel("Aieules")

plt.tight_layout()

# Sauvegarde dans un fichier image
plt.savefig("graphes_population.png")
print("✅ Graphes sauvegardés dans le fichier 'graphes_population.png'")

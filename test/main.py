import matplotlib
matplotlib.use('TkAgg')

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

population_df, aieux_df, aieules_df = dataframes

# Tracer un seul graphe avec toutes les courbes
plt.figure(figsize=(12, 7))

# Courbe 1 : Population (ligne simple)
plt.plot(population_df['Temps'], population_df['Taille de la population'],
         color='steelblue', linestyle='-', label="taille de la population")

# Courbe 2 : Aieules (lignes + carrés)
plt.plot(aieules_df['Temps'], aieules_df['Aieules'],
         color='brown', marker='s', linestyle='-', markersize=5, label="aïeules")

# Courbe 3 : Aieux (lignes + carrés)
plt.plot(aieux_df['Temps'], aieux_df['Aieux'],
         color='olive', marker='s', linestyle='-', markersize=5, label="aïeux")

# Log scale Y si besoin
plt.yscale('log')

# Titre et légendes
plt.xlabel("(1000 ans)", fontsize=12, fontweight='bold')
plt.legend()
plt.grid(True, which="both", linestyle="--", alpha=0.5)

# Ajustement des marges
plt.tight_layout()

# Sauvegarde dans un fichier image
plt.savefig("graphes_population.png")
print("✅ Graphes sauvegardés dans le fichier 'graphes_population.png'")

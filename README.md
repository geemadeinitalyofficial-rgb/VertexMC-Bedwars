# VertexMC Bedwars 🛏️⚔️

Plugin BedWars per Minecraft **1.21+** (Paper/Spigot) sviluppato da VertexMC Network.

---

## Compatibile con PlugManX

## Funzionalità

### Gameplay
- Sistema **multi-arena** con configurazione in-game
- Team da 2 a 8 (RED, BLUE, GREEN, YELLOW, AQUA, WHITE, PINK, GRAY)
- Modalità 1v1 / 2v2 / 3v3 / 4v4 configurabile
- **Rispawn automatico** con timer (con letto) o eliminazione definitiva (senza letto)
- **Void kill** configurabile
- **Fuoco amico** disabilitato automaticamente

### Generatori
| Risorsa | Delay default | Max |
|---------|--------------|-----|
| Ferro   | 2s           | 32  |
| Oro     | 8s           | 16  |
| Diamante| 30s          | 4   |
| Smeraldo| 60s          | 2   |

- **Upgrade automatici** dei generatori di diamante (Tier II a 5min, Tier III a 10min)
- Ologrammi sui generatori con nome e tier
- **Forgia team** che velocizza i generatori di ferro/oro della propria base

### Shop
- **Blocchi**: Legno, Terracotta, End Stone, Scale, Ossidiana, Vetro
- **Armi**: Spade (legno → ferro → diamante), Arco (3 livelli), Frecce
- **Armature**: Cotta → Ferro → Diamante (stivali + pantaloni)
- **Strumenti**: Picconi (legno → pietra → ferro → diamante), Ascia, Forbici
- **Speciali**: Mela d'oro, Perla dell'Ender, TNT, Palla di fuoco, Ragnatela, Acciarino, Pozioni
- Upgrade di **Affilatura** e **Protezione** applicati automaticamente agli oggetti acquistati

### Upgrade Team (Villager dedicato)
| Upgrade | Max Tier | Costo |
|---------|----------|-------|
| Affilatura | IV | 2/4/8/16 Diamanti |
| Protezione Armatura | IV | 2/4/8/16 Diamanti |
| Forgia Migliorata | IV | 2/4/8/16 Diamanti |
| Alacrità (Haste) | II | 2/4 Diamanti |
| Pool di Cura | I | 1 Smeraldo |
| Trappola Pronto Soccorso | — | 1 Smeraldo |
| Trappola Disturbo Minatore | — | 1 Smeraldo |
| Trappola Allarme | — | 1 Smeraldo |

### Scoreboard
- In attesa: nome arena, giocatori, countdown
- In gioco: stato letti di tutti i team, kill, timer partita

### Statistiche
- Vittorie, sconfitte, kill, morti, K/D, letti distrutti, partite giocate
- Salvate in `stats.yml`, persistenti tra i riavvii

---

## 📦 Installazione

1. **Compilare** con Maven:
   ```bash
   mvn clean package
   ```
2. Copia il `.jar` in `plugins/`
3. Avvia il server (si genera la configurazione)
4. Configura le arene (vedi sotto)

**Requisiti**: Paper 1.21+ (o Spigot 1.21+), Java 21

---

## 🗺️ Setup Arena (passo-passo)

```
# 1. Crea l'arena
/bwsetup create miaArena

# 2. Vai nella lobby dell'arena e imposta lo spawn
/bwsetup setlobby miaArena

# 3. Vai allo spawn di ogni team
/bwsetup setspawn miaArena RED
/bwsetup setspawn miaArena BLUE
/bwsetup setspawn miaArena GREEN
/bwsetup setspawn miaArena YELLOW

# 4. Vai vicino al letto di ogni team
/bwsetup setbed miaArena RED
/bwsetup setbed miaArena BLUE
...

# 5. Aggiungi i generatori
/bwsetup adddiamondgen miaArena   # (fallo dove vuoi il generatore diamante)
/bwsetup addemeraldgen miaArena

# 6. Aggiungi i villager
/bwsetup addshop miaArena         # dove vuoi il villager shop
/bwsetup addupgrade miaArena      # dove vuoi il villager upgrade

# 7. Configura team e dimensioni
/bwsetup setteams miaArena 4      # 4 team
/bwsetup setsize miaArena 2       # 2 giocatori per team

# 8. Imposta spettatori
/bwsetup setspectator miaArena

# 9. Finalizza
/bwsetup done miaArena

# Controlla tutto
/bwsetup info miaArena
```

---

## 💬 Comandi

| Comando | Descrizione | Permesso |
|---------|-------------|----------|
| `/bw list` | Lista arene | `vertexbedwars.play` |
| `/bw join <arena>` | Entra in una partita | `vertexbedwars.play` |
| `/bwjoin <arena>` | Alias join | `vertexbedwars.play` |
| `/bwleave` | Esci dalla partita | `vertexbedwars.play` |
| `/bwstats [player]` | Statistiche | `vertexbedwars.play` |
| `/bwsetup ...` | Configurazione arene | `vertexbedwars.admin` |
| `/bw reload` | Ricarica config | `vertexbedwars.admin` |

---

## ⚙️ config.yml

```yaml
settings:
  lobby-world: "lobby"
  min-players: 2
  max-wait-time: 60       # secondi attesa
  respawn-time: 5         # secondi rispawn
  void-kill-height: -10
```

---

## 📁 Struttura file
```
plugins/VertexBedwars/
├── config.yml
├── stats.yml
└── arenas/
    ├── miaArena.yml
    └── altraArena.yml
```

---

##  Architettura progetto source

```
it.vertex.bedwars
├── VertexBedwars.java          (Main)
├── game/
│   ├── BedwarsGame.java        (Logica di gioco)
│   ├── Arena.java              (Config arena + I/O)
│   ├── BedwarsTeam.java        (Enum team)
│   ├── TeamData.java           (Stato team in partita)
│   └── GameState.java          (Enum stati)
├── generators/
│   └── ResourceGenerator.java  (Generatori risorse)
├── managers/
│   ├── ArenaManager.java
│   ├── GameManager.java
│   ├── ShopManager.java
│   ├── StatsManager.java
│   └── ScoreboardManager.java
├── listeners/
│   ├── GameListener.java       (Morte, danno, chat)
│   ├── BedListener.java        (Rottura letti)
│   ├── ShopListener.java       (Villager shop/upgrade)
│   ├── BlockListener.java      (Blocchi piazzati/rotti)
│   └── PlayerListener.java     (Quit, drop)
├── commands/
│   ├── BedwarsCommand.java
│   ├── JoinCommand.java
│   ├── LeaveCommand.java
│   ├── StatsCommand.java
│   └── SetupCommand.java
└── utils/
    ├── ItemBuilder.java
    └── MessageUtil.java
```
## Made by &44 whit ❤️

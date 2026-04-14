"""
Generate textures for the Stables building:
  1. Block texture (16x16) - hut block top + side
  2. GUI icon (32x32) - building list icon in town hall
  3. Hut icon (16x16) - building icon used in Blockout GUI

Uses only Python builtins.
"""
import struct, zlib, os

def create_png(width, height, pixels):
    """Create a minimal PNG from RGBA pixel grid."""
    def chunk(ctype, data):
        c = ctype + data
        return struct.pack('>I', len(data)) + c + struct.pack('>I', zlib.crc32(c) & 0xffffffff)
    raw = b''
    for y in range(height):
        raw += b'\x00'
        for x in range(width):
            r, g, b, a = pixels[y][x]
            raw += struct.pack('BBBB', r, g, b, a)
    sig = b'\x89PNG\r\n\x1a\n'
    ihdr = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)
    return sig + chunk(b'IHDR', ihdr) + chunk(b'IDAT', zlib.compress(raw)) + chunk(b'IEND', b'')

T = (0, 0, 0, 0)

# Color palette for stables
WOOD_DARK  = (101, 67, 33, 255)
WOOD_MED   = (139, 90, 43, 255)
WOOD_LT    = (165, 115, 60, 255)
WOOD_HI    = (185, 140, 80, 255)
HAY        = (218, 190, 80, 255)
HAY_DARK   = (180, 155, 50, 255)
HAY_LT     = (240, 215, 110, 255)
STONE      = (136, 136, 136, 255)
STONE_DK   = (110, 110, 110, 255)
STONE_LT   = (165, 165, 165, 255)
IRON       = (180, 180, 180, 255)
IRON_DK    = (140, 140, 140, 255)
BLACK      = (30, 30, 30, 255)
BROWN_DK   = (80, 52, 25, 255)
BROWN_HIDE = (150, 100, 55, 255)
WHITE      = (240, 240, 240, 255)
ROOF_RED   = (160, 60, 50, 255)
ROOF_DK    = (130, 45, 35, 255)
ROOF_LT    = (185, 80, 65, 255)

# ========== 1. Block side texture (16x16) - wooden planks with hay ==========
block_side = [[WOOD_MED]*16 for _ in range(16)]
# Plank pattern
for y in range(16):
    for x in range(16):
        if y % 4 == 0:
            block_side[y][x] = WOOD_DARK
        elif x % 8 == 0:
            block_side[y][x] = WOOD_DARK
        elif (y % 4 == 2) and (x % 8 == 4):
            block_side[y][x] = WOOD_DARK
        elif y % 4 == 1 and x % 3 == 0:
            block_side[y][x] = WOOD_LT
# Hay bottom strip
for y in range(12, 16):
    for x in range(16):
        if (x + y) % 3 == 0:
            block_side[y][x] = HAY
        elif (x + y) % 3 == 1:
            block_side[y][x] = HAY_DARK
        else:
            block_side[y][x] = HAY_LT

# ========== 2. Block top texture (16x16) - wooden planks ==========
block_top = [[WOOD_MED]*16 for _ in range(16)]
for y in range(16):
    for x in range(16):
        if x % 4 == 0:
            block_top[y][x] = WOOD_DARK
        elif y % 8 == 0:
            block_top[y][x] = WOOD_DARK
        elif (x % 4 == 2) and (y % 8 == 4):
            block_top[y][x] = WOOD_DARK
        elif x % 4 == 1 and y % 3 == 0:
            block_top[y][x] = WOOD_LT

# ========== 3. GUI icon (32x32) - stables building with horse silhouette ==========
gui = [[T]*32 for _ in range(32)]

# Roof (rows 0-8)
for y in range(0, 9):
    left = max(0, 6 - y)
    right = min(31, 25 + y)
    for x in range(left, right + 1):
        if y % 3 == 0:
            gui[y][x] = ROOF_DK
        elif (x + y) % 4 == 0:
            gui[y][x] = ROOF_LT
        else:
            gui[y][x] = ROOF_RED

# Walls (rows 9-24)
for y in range(9, 25):
    for x in range(4, 28):
        if x == 4 or x == 27:
            gui[y][x] = WOOD_DARK
        elif y == 9:
            gui[y][x] = WOOD_DARK
        else:
            gui[y][x] = WOOD_MED
            if (x + y) % 5 == 0:
                gui[y][x] = WOOD_LT

# Door (center)
for y in range(15, 25):
    for x in range(13, 19):
        gui[y][x] = BROWN_DK
# Door handle
gui[20][17] = IRON

# Windows (left and right)
for y in range(12, 17):
    for x in range(7, 11):
        gui[y][x] = (150, 200, 230, 255)  # glass blue
    for x in range(21, 25):
        gui[y][x] = (150, 200, 230, 255)

# Window frames
for x in range(7, 11):
    gui[12][x] = WOOD_DARK
    gui[16][x] = WOOD_DARK
for y in range(12, 17):
    gui[y][7] = WOOD_DARK
    gui[y][10] = WOOD_DARK
for x in range(21, 25):
    gui[12][x] = WOOD_DARK
    gui[16][x] = WOOD_DARK
for y in range(12, 17):
    gui[y][21] = WOOD_DARK
    gui[y][24] = WOOD_DARK

# Foundation (rows 25-27)
for y in range(25, 28):
    for x in range(3, 29):
        if (x + y) % 2 == 0:
            gui[y][x] = STONE
        else:
            gui[y][x] = STONE_DK

# Horseshoe symbol above door (simple U shape)
hs_color = IRON
gui[10][14] = hs_color
gui[10][17] = hs_color
gui[11][13] = hs_color
gui[11][18] = hs_color
gui[12][13] = hs_color
gui[12][18] = hs_color
gui[13][14] = hs_color
gui[13][17] = hs_color
gui[14][15] = hs_color
gui[14][16] = hs_color

# Hay bales on ground
for y in range(28, 31):
    for x in range(1, 6):
        gui[y][x] = HAY if (x+y) % 2 == 0 else HAY_DARK
    for x in range(26, 31):
        gui[y][x] = HAY if (x+y) % 2 == 0 else HAY_DARK

# ========== 4. Hut icon (16x16) - simplified stables icon ==========
icon = [[T]*16 for _ in range(16)]

# Roof (rows 0-4)
for y in range(0, 5):
    left = max(0, 4 - y)
    right = min(15, 11 + y)
    for x in range(left, right + 1):
        icon[y][x] = ROOF_RED if (x+y) % 3 != 0 else ROOF_DK

# Walls (rows 5-12)
for y in range(5, 13):
    for x in range(2, 14):
        if x == 2 or x == 13:
            icon[y][x] = WOOD_DARK
        elif y == 5:
            icon[y][x] = WOOD_DARK
        else:
            icon[y][x] = WOOD_MED
            if (x+y) % 4 == 0:
                icon[y][x] = WOOD_LT

# Door
for y in range(8, 13):
    for x in range(6, 10):
        icon[y][x] = BROWN_DK
icon[10][9] = IRON

# Horseshoe above door
icon[6][7] = IRON
icon[6][8] = IRON
icon[7][6] = IRON
icon[7][9] = IRON

# Foundation (rows 13-14)
for y in range(13, 15):
    for x in range(1, 15):
        icon[y][x] = STONE if (x+y) % 2 == 0 else STONE_DK

# Hay
for x in range(0, 3):
    icon[15][x] = HAY
for x in range(13, 16):
    icon[15][x] = HAY

# ========== Save all textures ==========
base = r'e:\repos\MineColonies_MountedKnight\src\main\resources\assets\mounted_knight\textures'

paths = {
    'blocks/stables_side.png':      (16, block_side),
    'blocks/stables_top.png':       (16, block_top),
    'gui/stables_icon.png':         (32, gui),
    'gui/stables_hut_icon.png':     (16, icon),
}

for rel_path, (size, pxdata) in paths.items():
    full = os.path.join(base, rel_path)
    d = os.path.dirname(full)
    if not os.path.exists(d):
        os.makedirs(d)
    data = create_png(size, size, pxdata)
    with open(full, 'wb') as f:
        f.write(data)
    print('OK: {} ({} bytes)'.format(rel_path, len(data)))

print('All stables textures generated!')

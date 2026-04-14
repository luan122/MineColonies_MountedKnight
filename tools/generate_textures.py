"""
Generate 16x16 PNG textures for all 8 lance variants.
Uses only Python builtins (struct, zlib) - no PIL/Pillow needed.
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

T = (0, 0, 0, 0)  # transparent

# Shaft colors
SHAFT      = (139, 90, 43, 255)
SHAFT_HI   = (165, 115, 60, 255)
GRIP       = (101, 67, 33, 255)
GRIP_DARK  = (80, 52, 25, 255)

# Material colors: (main, highlight, shadow)
materials = {
    'wooden':           ((153, 127, 80, 255),  (180, 155, 105, 255), (120, 98, 58, 255)),
    'stone':            ((136, 136, 136, 255), (175, 175, 175, 255), (100, 100, 100, 255)),
    'iron':             ((200, 200, 200, 255), (225, 225, 225, 255), (160, 160, 160, 255)),
    'golden':           ((252, 215, 35, 255),  (255, 240, 100, 255), (200, 168, 10, 255)),
    'diamond':          ((80, 220, 210, 255),  (140, 240, 235, 255), (45, 175, 168, 255)),
    'netherite':        ((80, 72, 72, 255),    (112, 100, 100, 255), (50, 44, 44, 255)),
    'osmium':           ((100, 180, 205, 255), (145, 212, 230, 255), (65, 140, 165, 255)),
    'refined_obsidian': ((110, 50, 140, 255),  (150, 85, 180, 255),  (72, 30, 100, 255)),
}

# Lance pixel art: (x, y, color_key)
# H=head main, L=head highlight, h=head shadow
# S=shaft, s=shaft highlight, G=grip, g=grip dark
lance_art = [
    # Pointed head (top-right)
    (14, 0, 'L'),
    (13, 1, 'H'), (14, 1, 'L'),
    (12, 2, 'H'), (13, 2, 'H'),
    (11, 3, 'h'), (12, 3, 'H'),
    (10, 4, 'h'), (11, 4, 'H'),
    # Cross-guard
    (10, 5, 'h'), (9, 4, 'h'),
    # Shaft (diagonal)
    (9, 5, 'S'), (10, 5, 's'),
    (8, 6, 'S'), (9, 6, 's'),
    (7, 7, 'S'), (8, 7, 's'),
    (6, 8, 'S'), (7, 8, 's'),
    (5, 9, 'S'), (6, 9, 's'),
    (4, 10, 'S'), (5, 10, 's'),
    # Grip (bottom-left)
    (3, 11, 'G'), (4, 11, 'g'),
    (2, 12, 'G'), (3, 12, 'g'),
    (1, 13, 'G'), (2, 13, 'g'),
    (0, 14, 'G'), (1, 14, 'g'),
    (0, 15, 'g'),
]

out = r'e:\repos\MineColonies_MountedKnight\src\main\resources\assets\mounted_knight\textures\item'
os.makedirs(out, exist_ok=True)

for name, (main, highlight, shadow) in materials.items():
    pixels = [[T]*16 for _ in range(16)]
    cmap = {'H': main, 'L': highlight, 'h': shadow,
            'S': SHAFT, 's': SHAFT_HI, 'G': GRIP, 'g': GRIP_DARK}
    for x, y, ct in lance_art:
        pixels[y][x] = cmap[ct]
    data = create_png(16, 16, pixels)
    fname = '{}_lance.png'.format(name)
    path = os.path.join(out, fname)
    with open(path, 'wb') as f:
        f.write(data)
    print('OK: {} ({} bytes)'.format(fname, len(data)))

print('All 8 textures generated!')

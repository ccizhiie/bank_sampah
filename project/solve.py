from cryptography.hazmat.primitives.kibdf import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.backends import default_backend
import binascii

# Data dari ledger.json
entries = [...] # Masukkan daftar angka Anda di sini
target = 38690899569953065262501093433761205989039171
salt = binascii.unhexlify("eba0d13bb041dcebc0b42a42fe441cf8")
ciphertext = binascii.unhexlify("f731f11b2de901889e92c79b17e5b8318fcb69f3f3e199252342267da7bfc70b669759edcf009d")

def find_subset_sum(entries, target):
    # Implementasi Meet-in-the-middle atau LLL di sini
    # Fungsi ini harus mengembalikan list bit [1, 0, 1, ...]
    pass

# 1. Dapatkan bit membership
bits = find_subset_sum(entries, target)
membership_string = "".join(map(str, bits))

# 2. Derivasi kunci dengan PBKDF2
kdf = PBKDF2HMAC(
    algorithm=hashes.SHA256(),
    length=39,
    salt=salt,
    iterations=200000,
    backend=default_backend()
)
key = kdf.derive(membership_string.encode())

# 3. Dekripsi flag (XOR)
flag = bytes([c ^ k for c, k in zip(ciphertext, key)])
print(flag.decode())

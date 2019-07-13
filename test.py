
import glob, re

src_dir = './values-h700dp'
dst_dir = './values'
mag_ratio = 92.5 # %

def convertDimens(src_file, dst_file):
	a = ""
	with open(src_file) as f:
		for i in f:
			m = re.search(r'\s*<dimen name=.*>([0-9]+)(dp|sp)</dimen>', i.strip())
			if m:
				val = m.group(1)
				new_val = int(val) * mag_ratio / 100
				a = a + i.replace(str(val), str(new_val))
			else:
				a = a + i

	with open(dst_file, mode='w') as f:
		f.write(a)

def main():
	for src_file in glob.glob(src_dir + '/dimens*.xml'):
		dst_file = src_file.replace(src_dir, dst_dir)
		convertDimens(src_file, dst_file)
main()

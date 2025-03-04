/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EditorFrame.java
 *
 * Created on Nov 14, 2011, 3:30:41 PM
 */

package steve;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.openrs.cache.Cache;
import net.openrs.cache.Container;
import net.openrs.cache.FileStore;
import net.openrs.cache.def.RSInterface;
import net.openrs.cache.sprite.Sprite;

/**
 *
 * @author Stephen
 */
public class SpriteEditorFrame extends javax.swing.JFrame {
	private DefaultMutableTreeNode spritesNode = new DefaultMutableTreeNode("Sprites");
	private DefaultTreeModel treeModel = new DefaultTreeModel(spritesNode);
	private DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Frames");
	private DefaultTreeModel childDefaultModel = new DefaultTreeModel(node1);
	private DefaultMutableTreeNode interfaceNode = new DefaultMutableTreeNode("Interfaces");
	private DefaultTreeModel childModel = new DefaultTreeModel(interfaceNode);

	private Cache cache;
	private int spriteId;
	private Sprite sprite;
	private int frameIndex;
	private ArrayList<String> items = new ArrayList<String>();

	/** Creates new form EditorFrame */
	public SpriteEditorFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		loadRecents();
		initComponents();
	}

	private void loadRecents() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./data/recents.txt"));
			String s;
			while ((s = reader.readLine()) != null) {
				items.add(s);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void populateLists() {
		try {
			jMenuItem3.setEnabled(true);
			jMenuItem4.setEnabled(true);
			for (int i = 0; i < cache.getFileCount(8); i++) {
				spritesNode.add(new DefaultMutableTreeNode(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		spriteTree = new javax.swing.JTree();
		jSeparator2 = new javax.swing.JSeparator();
		jPanel3 = new javax.swing.JPanel();
		jScrollPane2 = new javax.swing.JScrollPane();
		frameTree = new javax.swing.JTree();
		jPanel5 = new javax.swing.JPanel();
		addButton = new javax.swing.JButton();
		deleteButton = new javax.swing.JButton();
		replaceButton = new javax.swing.JButton();
		popoutButton = new javax.swing.JButton();
		jButton1 = new javax.swing.JButton();
		jScrollPane3 = new javax.swing.JScrollPane();
		interfaceCrossReferenceDialog = new JDialog();
		jPanel4 = new javax.swing.JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				if (sprite != null) {
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, jPanel4.getWidth(), jPanel4.getHeight());
					g.drawImage(sprite.images.get(frameIndex), 0, 0, null);
				}
			}
		};
		addSpriteButton = new javax.swing.JButton();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu1 = new javax.swing.JMenu();
		jMenuItem1 = new javax.swing.JMenuItem();
		jMenu2 = new javax.swing.JMenu();
		jMenuItem3 = new javax.swing.JMenuItem();
		jMenuItem4 = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JPopupMenu.Separator();
		jMenuItem2 = new javax.swing.JMenuItem();
		searchField = new JTextField();
		childTree = new JTree(childModel);

		setTitle("RuneScape Cache Sprite Editor");
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent evt) {
				formWindowClosed(evt);
			}
		});

		interfaceCrossReferenceDialog.setTitle("Interface Cross Reference");
		interfaceCrossReferenceDialog.setPreferredSize(new Dimension(100, 400));
		interfaceCrossReferenceDialog.setResizable(false);

		childTree.setModel(childModel);
		childTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				childTreeChanged(arg0);
			}

		});
		interfaceCrossReferenceDialog.add(childTree, BorderLayout.CENTER);
		interfaceCrossReferenceDialog.pack();

		getContentPane().setLayout(new java.awt.CardLayout());

		jLabel1.setText("No cache opened. Please select File > Load cache to load your cache.");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jLabel1)
						.addContainerGap(218, Short.MAX_VALUE))
				);
		jPanel1Layout.setVerticalGroup(
				jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jLabel1)
						.addContainerGap(455, Short.MAX_VALUE))
				);

		getContentPane().add(jPanel1, "card2");

		jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
		jLabel2.setText("Sprites");

		spriteTree.setModel(treeModel);
		spriteTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
				spriteTreeValueChanged(evt);
			}
		});
		jScrollPane1.setViewportView(spriteTree);

		jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Sprite options"));

		frameTree.setModel(childDefaultModel);
		frameTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
				frameTreeValueChanged(evt);
			}
		});
		jScrollPane2.setViewportView(frameTree);

		jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

		addButton.setText("Add new frame");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addButtonActionPerformed(evt);
			}
		});

		deleteButton.setText("Delete selected frame");
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButtonActionPerformed(evt);
			}
		});

		replaceButton.setText("Replace selected frame");
		replaceButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				replaceButtonActionPerformed(evt);
			}
		});

		popoutButton.setText("Save images");
		popoutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				popoutButtonActionPerformed(evt);
			}
		});

		jButton1.setText("Pack to cache");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(
				jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel5Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
								.addComponent(replaceButton, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
								.addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
								.addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
								.addComponent(popoutButton, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
								.addContainerGap())
				);
		jPanel5Layout.setVerticalGroup(
				jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel5Layout.createSequentialGroup()
						.addComponent(addButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(deleteButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(replaceButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(popoutButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jButton1))
				);

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(
				jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 341, Short.MAX_VALUE)
				);
		jPanel4Layout.setVerticalGroup(
				jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 213, Short.MAX_VALUE)
				);

		jScrollPane3.setViewportView(jPanel4);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(
				jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
										.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addContainerGap())
				);
		jPanel3Layout.setVerticalGroup(
				jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
								.addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGap(7, 7, 7)
								.addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addContainerGap())
				);

		addSpriteButton.setText("Add new sprite");
		addSpriteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addSpriteButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel2Layout.createSequentialGroup()
										.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(addSpriteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
												.addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(6, 6, 6)
												.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addComponent(jLabel2))
												.addContainerGap())
				);
		jPanel2Layout.setVerticalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jLabel2)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
								.addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
										.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(addSpriteButton)))
										.addContainerGap())
				);

		getContentPane().add(jPanel2, "card3");

		jMenu1.setText("File");

		jMenuItem1.setText("Load cache");
		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem1ActionPerformed(evt);
			}
		});
		jMenu1.add(jMenuItem1);
		jMenu1.add(jSeparator1);
		for (final String item : items) {
			JMenuItem menuItem = new JMenuItem();
			menuItem.setText(item);
			menuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					openCache(item);
				}
			});
			jMenu1.add(menuItem);
		}
		jMenu1.add(new JPopupMenu.Separator());
		jMenuItem2.setText("Quit");
		jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem2ActionPerformed(evt);
			}
		});
		jMenu1.add(jMenuItem2);
		//
		jMenu2.setText("Tools");

		jMenuItem3.setText("Dump sprites");
		jMenuItem3.setEnabled(false);
		jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem3ActionPerformed(evt);
			}
		});
		jMenu2.add(jMenuItem3);

		jMenuItem4.setText("Interface Cross Referencer");
		jMenuItem4.setEnabled(false);
		jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem4ActionPerformed(evt);
			}
		});
		jMenu2.add(jMenuItem4);

		jMenuBar1.add(jMenu1);
		jMenuBar1.add(jMenu2);

		setJMenuBar(jMenuBar1);

		pack();
	}// </editor-fold>

	boolean loadedInterfaces = false;

	protected void jMenuItem4ActionPerformed(ActionEvent evt) {
		/*if (!loadedInterfaces) {
			interfaceNode.add(new DefaultMutableTreeNode("Loading..."));
			childModel.reload(interfaceNode);
			interfaceCrossReferenceDialog.setVisible(true);
			Thread loader = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						interfaceNode = new DefaultMutableTreeNode("Interfaces");
						for (int i = 0; i < cache.getFileCount(3); i++) {
							RSInterface rsInterface = RSInterface.get(cache, i);
							if (rsInterface != null) {
								DefaultMutableTreeNode node = new DefaultMutableTreeNode(i);
								for (int childId = 0; childId < rsInterface.children.size(); childId++) {
									RSInterface child = rsInterface.children.get(childId);
									node.add(new DefaultMutableTreeNode(childId + " - " + RSInterface.getChildTypeName(child.contentType)));
								}

								System.out.println(i);
								interfaceNode.add(node);
								childModel.reload(interfaceNode);
								Thread.sleep(5);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			});
			loader.start();
		} else {*/

		int i = Integer.parseInt(JOptionPane.showInputDialog("What interface do you want to load?"));
		RSInterface rsInterface = RSInterface.get(cache, i);
		if (rsInterface != null) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(i);
			for (int childId = 0; childId < rsInterface.children.size(); childId++) {
				RSInterface child = rsInterface.children.get(childId);
				node.add(new DefaultMutableTreeNode(childId + " - " + RSInterface.getChildTypeName(child.contentType)));
			}
			interfaceNode.add(node);
			childModel.reload(interfaceNode);
		} else {
			JOptionPane.showMessageDialog(this, "Error in loading interface");
			return;
		}
		interfaceCrossReferenceDialog.setAlwaysOnTop(true);
		interfaceCrossReferenceDialog.setVisible(true);
		//}

	}

	protected void jMenuItem3ActionPerformed(ActionEvent evt) {
		if (JOptionPane.showConfirmDialog(this, "This may take awhile, and will create a lot of folders and files on your computer, are you sure?") == 0) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				final File file = chooser.getSelectedFile();
				if (file.isDirectory()) {
					try {
						final JDialog dialog = new JDialog();
						dialog.setTitle("Dump sprites");
						dialog.setSize(100, 100);
						dialog.setLayout(new FlowLayout());
						final JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL);
						bar.setMaximum(cache.getFileCount(8));
						bar.setValue(0);
						bar.setStringPainted(true);
						bar.setBorder(new EmptyBorder(25, 25, 25, 25));
						dialog.add(bar);
						dialog.setVisible(true);
						dialog.setAlwaysOnTop(true);
						Thread thread = new Thread() {
							public void run() {
								try {
									for (int i = 0; i < 1455; i++) {
										Sprite sprite = Sprite.get(cache, i);
										bar.setString("Writing " + sprite.id);
										File newDir = new File(file.getAbsolutePath() + "/Sprite "+sprite.id);
										if (!newDir.exists())
											newDir.mkdir();
										for (int x = 0; x < cache.getFileCount(8); x++) {
											if (sprite.images.get(x) == null)
												continue;
											File newFile = new File(newDir.getAbsolutePath() + "/Child image " + x + ".png");
											if (!newFile.exists())
												newFile.createNewFile();
											ImageIO.write(sprite.images.get(x), "PNG", newFile);			
										}
										bar.setString("Done " + sprite.id);
										bar.setValue(i);
										Thread.sleep(5);
									}
									dialog.setVisible(false);
								} catch (Exception e) {

								}
							}
						};
						thread.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(this, "Make sure you select a folder.");
				}
			}
		}
	}

	protected void jButton1ActionPerformed(ActionEvent evt) {
		try {
			cache.write(8, spriteId, new Container(sprite.container.getType(), sprite.encode(), sprite.container.getVersion()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void openCache(String item) {
		File file = new File(item);
		if (file.isDirectory() && file.exists()) {
			try {
				this.cache = new Cache(FileStore.open(file));
				populateLists();
				treeModel.reload(spritesNode);
				((java.awt.CardLayout) getContentPane().getLayout()).next(getContentPane());
			} catch (IOException e) {

			}
		} else {
			JOptionPane.showMessageDialog(this, "Make sure the cache you select is a directory and not a file, and exists as well");
		}

	}

	protected void formWindowClosed(WindowEvent evt) {
		try {
			cache.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void addSpriteButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				BufferedImage image = ImageIO.read(chooser.getSelectedFile());
				sprite = Sprite.addSprite(cache, image);
				spritesNode.add(new DefaultMutableTreeNode(cache.getFileCount(8) - 1));
				treeModel.reload(spritesNode);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error loading image, please make sure it's an image file.");
			}
		}
	}

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				for (File file : chooser.getSelectedFiles()) {
					BufferedImage image = ImageIO.read(file);
					sprite.images.add(image);
				}
				node1.removeAllChildren();
				for (int i = 0; i < sprite.images.size(); i++) {
					node1.add(new DefaultMutableTreeNode(i));
				}
				childDefaultModel.reload(node1);
				frameTree.scrollRowToVisible(1); //go the the first element in the list
				frameTree.setSelectionRow(1);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error loading image, please make sure it's an image file.");
			}
		}
	}

	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			sprite.images.remove(frameIndex);
			node1.removeAllChildren();
			for (int i = 0; i < sprite.images.size(); i++) {
				node1.add(new DefaultMutableTreeNode(i));
			}
			childDefaultModel.reload(node1);
			frameTree.scrollRowToVisible(1); //go the the first element in the list
			frameTree.setSelectionRow(1);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error loading image, please make sure it's an image file.");
		}
	}

	private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				BufferedImage image = ImageIO.read(chooser.getSelectedFile());
				sprite.images.set(frameIndex, image);
				node1.removeAllChildren();
				for (int i = 0; i < sprite.images.size(); i++) {
					node1.add(new DefaultMutableTreeNode(i));
				}
				childDefaultModel.reload(node1);
				frameTree.scrollRowToVisible(1); //go the the first element in the list
				frameTree.setSelectionRow(1);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error loading image, please make sure it's an image file.");
			}
		}
	}

	private void popoutButtonActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file.isDirectory()) {
				try {
					for (int i = 0; i < sprite.images.size(); i++) {
						if (sprite.images.get(i) != null) {
							File newFile = new File(file.getAbsolutePath() + "/S" +sprite.id+"I"+i+".png");
							if (!newFile.exists())
								newFile.createNewFile();
							ImageIO.write(sprite.images.get(i), "PNG", newFile);	
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(this, "Make sure you select a folder.");
			}
		}
	}

	private void frameTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {
		try {
			frameIndex = Integer.parseInt(frameTree.getLastSelectedPathComponent().toString());
			if (sprite.images.get(frameIndex).getWidth() > jPanel4.getWidth() || sprite.images.get(frameIndex).getHeight()> jPanel4.getHeight())
				jPanel4.setPreferredSize(new Dimension(sprite.images.get(frameIndex).getWidth(), sprite.images.get(frameIndex).getHeight()));
			jPanel4.revalidate();
			jPanel4.repaint();
		} catch (Exception e) {

		}
	}


	protected void childTreeChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) childTree.getLastSelectedPathComponent();
		int interfaceId = 0;
		int childId = 0;
		if (node == null)
			return;
		else if (node.getLevel() == 0)
			return;
		else if (node.getLevel() == 1)
			return;
		else if (node.getLevel() == 2)
			childId = Integer.parseInt(node.toString().split("-")[0].replaceAll(" ", ""));
		if (!node.toString().split("-")[1].contains("Sprite"))
			return;
		interfaceId = Integer.parseInt(node.getParent().toString());
		RSInterface rsInterface = RSInterface.get(cache, interfaceId);
		if (rsInterface != null) {
			RSInterface child = rsInterface.children.get(childId);
			TreePath path = new TreePath(spritesNode.getChildAt(child.spriteId));
			spriteTree.scrollPathToVisible(path);
			spriteTree.setSelectionPath(path);
		}
	}

	private void spriteTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {
		try {
			spriteId = Integer.parseInt(spriteTree.getLastSelectedPathComponent().toString());
			sprite = Sprite.get(cache, spriteId);
			node1.removeAllChildren();
			for (int i = 0; i < sprite.images.size(); i++) {
				node1.add(new DefaultMutableTreeNode(i));
			}
			childDefaultModel.reload(node1);
			frameTree.scrollRowToVisible(1); //go the the first element in the list
			frameTree.setSelectionRow(1);
		} catch (Exception e) {
		}
	}

	private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file.isDirectory()) {
				try {
					if (!items.contains(file.getAbsolutePath()))
						addRecent(file.getAbsolutePath());
					this.cache = new Cache(FileStore.open(file));
					populateLists();
					treeModel.reload(spritesNode);
					((java.awt.CardLayout) getContentPane().getLayout()).next(getContentPane());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(this, "Make sure the cache you select is a directory and not a file");
			}
		}
	}

	private void addRecent(String absolutePath) {
		try {
			items.add(absolutePath);
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/recents.txt"));
			for (String s : items) {
				writer.write(s);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
		if (JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?") == 0) {
			System.exit(0);
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new SpriteEditorFrame().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	// Variables declaration - do not modify
	private javax.swing.JButton addButton;
	private javax.swing.JButton addSpriteButton;
	private javax.swing.JButton deleteButton;
	private javax.swing.JTree frameTree;
	private javax.swing.JButton jButton1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenu jMenu2;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JMenuItem jMenuItem1;
	private javax.swing.JMenuItem jMenuItem2;
	private javax.swing.JMenuItem jMenuItem3;
	private javax.swing.JMenuItem jMenuItem4;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JPopupMenu.Separator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JButton popoutButton;
	private javax.swing.JButton replaceButton;
	private javax.swing.JTree spriteTree;
	private JDialog interfaceCrossReferenceDialog;
	private JTextField searchField;
	private javax.swing.JTree childTree;
	// End of variables declaration

}

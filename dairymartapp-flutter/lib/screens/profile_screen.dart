import 'package:flutter/material.dart';
import '../../models/assignment.dart';
import '../../models/user.dart';
import '../../services/assignment_service.dart';
import '../../services/session_manager.dart';
import '../../services/user_service.dart';
import '../../theme/app_theme.dart';
import '../../widgets/load_error.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  bool _loading = true;
  bool _saving = false;
  bool _editing = false;
  String? _error;
  AppUser? _user;
  ShopSummary? _shop;

  final _shopName = TextEditingController();
  final _gstNumber = TextEditingController();
  final _panNumber = TextEditingController();
  final _aadharNumber = TextEditingController();

  bool get _isRetailer =>
      SessionManager.instance.current?.role == UserRole.retailer ||
      _user?.userTypeId == 3;

  @override
  void initState() {
    super.initState();
    _load();
  }

  @override
  void dispose() {
    _shopName.dispose();
    _gstNumber.dispose();
    _panNumber.dispose();
    _aadharNumber.dispose();
    super.dispose();
  }

  String _displayTax(String value) =>
      value == 'NA' ? '' : value;

  void _fillShopFields(ShopSummary shop) {
    _shopName.text = shop.shopName;
    _gstNumber.text = _displayTax(shop.gstNumber);
    _panNumber.text = _displayTax(shop.panNumber);
    _aadharNumber.text = _displayTax(shop.aadharNumber);
  }

  Future<void> _load() async {
    final session = SessionManager.instance.current;
    if (session == null) return;
    setState(() {
      _loading = true;
      _error = null;
      _editing = false;
    });
    try {
      final user = await UserService.instance.getUser(session.userId);
      ShopSummary? shop;
      if (session.role == UserRole.retailer || user.userTypeId == 3) {
        shop = await AssignmentService.instance.getShopForRetailer(session.userId);
        if (shop != null) _fillShopFields(shop);
      }
      if (mounted) {
        setState(() {
          _user = user;
          _shop = shop;
        });
      }
    } catch (_) {
      if (mounted) setState(() => _error = 'Could not load profile.');
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _saveShop() async {
    final shop = _shop;
    if (shop == null) return;
    setState(() => _saving = true);
    try {
      final updated = await AssignmentService.instance.updateShop(
        shop.copyWith(
          shopName: _shopName.text.trim().isEmpty ? shop.shopName : _shopName.text.trim(),
          gstNumber: _gstNumber.text.trim(),
          panNumber: _panNumber.text.trim(),
          aadharNumber: _aadharNumber.text.trim(),
        ),
      );
      if (!mounted) return;
      _fillShopFields(updated);
      setState(() {
        _shop = updated;
        _editing = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Shop tax details saved.')),
      );
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final user = _user;
    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
        actions: [
          if (_isRetailer && _shop != null && !_loading)
            TextButton(
              onPressed: _saving
                  ? null
                  : () {
                      if (_editing) {
                        _saveShop();
                      } else {
                        setState(() => _editing = true);
                      }
                    },
              child: Text(_editing ? 'Save' : 'Edit'),
            ),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? LoadError(message: _error!, onRetry: _load)
              : ListView(
                  padding: const EdgeInsets.all(16),
                  children: [
                    const CircleAvatar(
                      radius: 36,
                      backgroundColor: AppColors.primaryLight,
                      child: Icon(Icons.person, color: AppColors.primary, size: 36),
                    ),
                    const SizedBox(height: 16),
                    Text(user?.displayName ?? '',
                        textAlign: TextAlign.center,
                        style: const TextStyle(
                            fontSize: 22, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 24),
                    ListTile(
                        title: const Text('Phone'),
                        subtitle: Text(user?.phoneNumber ?? '')),
                    ListTile(
                        title: const Text('Email'),
                        subtitle: Text(user?.emailId ?? '—')),
                    ListTile(
                        title: const Text('Role'),
                        subtitle: Text(user?.userTypeDesc ?? '—')),
                    if (_isRetailer) ...[
                      const Divider(height: 32),
                      const Text(
                        'Shop & tax details',
                        style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
                      ),
                      const SizedBox(height: 8),
                      if (_shop == null)
                        const ListTile(
                          title: Text('No shop linked'),
                          subtitle: Text('Ask admin to add a shop for this retailer.'),
                        )
                      else ...[
                        _field('Shop name', _shopName),
                        _field('GST number', _gstNumber),
                        _field('PAN', _panNumber),
                        _field('Aadhaar', _aadharNumber),
                        if (_editing)
                          Padding(
                            padding: const EdgeInsets.only(top: 8),
                            child: OutlinedButton(
                              onPressed: _saving
                                  ? null
                                  : () {
                                      _fillShopFields(_shop!);
                                      setState(() => _editing = false);
                                    },
                              child: const Text('Cancel'),
                            ),
                          ),
                      ],
                    ],
                  ],
                ),
    );
  }

  Widget _field(String label, TextEditingController controller) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: TextField(
        controller: controller,
        enabled: _editing && !_saving,
        decoration: InputDecoration(
          labelText: label,
          border: const OutlineInputBorder(),
        ),
      ),
    );
  }
}
